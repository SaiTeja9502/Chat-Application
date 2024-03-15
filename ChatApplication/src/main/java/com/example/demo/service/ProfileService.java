package com.example.demo.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.ContactDTO;
import com.example.demo.dto.Notification;
import com.example.demo.dto.NotificationType;
import com.example.demo.exceptions.ConversationNotFoundException;
import com.example.demo.exceptions.InvalidRequestException;
import com.example.demo.model.Contact;
import com.example.demo.model.Conversation;
import com.example.demo.model.ConversationStatus;
import com.example.demo.model.UserConversation;
import com.example.demo.model.UserConversationId;
import com.example.demo.model.UserRole;
import com.example.demo.repository.ContactRepository;
import com.example.demo.repository.ConversationRepository;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.UserConversationRepository;
import com.example.demo.utils.JWTUtil;
import com.example.demo.utils.NotificationUtil;

@Service
public class ProfileService {
	
	@Autowired
	ContactRepository contactRepository;
	
	@Autowired
	ConversationRepository conversationRepository;
	
	@Autowired
	UserConversationRepository userConversationRepository;
	
	@Autowired
	MessageRepository messageRepository;
	
	@Autowired
	JWTUtil jwtUtil;
	
	@Autowired
	NotificationUtil notificationUtil;

	public void updateUserName(String userName, String token) {
		String contactId = jwtUtil.extractUsername(token);
		Optional<Contact> contactOptional = contactRepository.findById(Long.parseLong(contactId));
		contactOptional.orElseThrow(()-> new UsernameNotFoundException("Not Found: " + contactId));
		Contact contact = contactOptional.get();
		contact.setUserName(userName);
		contactRepository.save(contact);
	}
	
	public void updatePhoto(String token, MultipartFile file) throws IOException {
		String contactId = jwtUtil.extractUsername(token);
		Optional<Contact> contactOptional = contactRepository.findById(Long.parseLong(contactId));
		contactOptional.orElseThrow(()-> new UsernameNotFoundException("Not Found: " + contactId));
		Contact contact = contactOptional.get();
		contact.setProfilePhoto(file.getBytes());
		contactRepository.save(contact);
	}
	
	public void updatePhoneNumber(String phoneNumber, String token) {
		String contactId = jwtUtil.extractUsername(token);
		Optional<Contact> contactOptional = contactRepository.findById(Long.parseLong(contactId));
		contactOptional.orElseThrow(()-> new UsernameNotFoundException("Not Found: " + contactId));
		Contact contact = contactOptional.get();
		contact.setPhoneNumber(phoneNumber);
		contactRepository.save(contact);
	}

	public void addFriend(String token, String phoneNumber) {
		String contactId = jwtUtil.extractUsername(token);
		Optional<Contact> contactOptional = contactRepository.findById(Long.parseLong(contactId));
		contactOptional.orElseThrow(()-> new UsernameNotFoundException("Not Found: " + contactId));
		Contact contact = contactOptional.get();
		Optional<Contact> friendContactOptional = contactRepository.findByPhoneNumber(phoneNumber);
		friendContactOptional.orElseThrow(()-> new UsernameNotFoundException("Not Found: " + phoneNumber));
		Contact friend = friendContactOptional.get();
		
		Conversation conversation = conversationRepository.save(new Conversation());
		
		UserConversation myUserConversation = new UserConversation(new UserConversationId(contact.getContactId(), conversation.getConversationId()), contact, conversation, LocalDateTime.now(),
				null, ConversationStatus.ACTIVE, UserRole.USER);
		
		UserConversation otherUserConversation = new UserConversation(new UserConversationId(friend.getContactId(), conversation.getConversationId()), friend, conversation, LocalDateTime.now(),
				null, ConversationStatus.ACTIVE, UserRole.USER);
		
		userConversationRepository.save(myUserConversation);
		userConversationRepository.save(otherUserConversation);
		Notification notification = new Notification();
        notification.setType(NotificationType.ADDED);
        notification.setSenderId(contact.getContactId());
        List<ContactDTO> contactDTOs = new ArrayList<>();
        ContactDTO contactDTO = new ContactDTO(friend.getContactId(), friend.getUserName(), Base64.getEncoder().encodeToString(friend.getProfilePhoto()), friend.getStatus());
        contactDTOs.add(contactDTO);
        notification.setContacts(contactDTOs);
		notificationUtil.sendNotification(notification);	
	}
	
	public void removeFriend(String token, String phoneNumber) throws ConversationNotFoundException {
		String contactId = jwtUtil.extractUsername(token);
		Optional<Contact> contactOptional = contactRepository.findById(Long.parseLong(contactId));
		contactOptional.orElseThrow(()-> new UsernameNotFoundException("Not Found: " + contactId));
		Contact contact = contactOptional.get();
		Optional<Contact> friendContactOptional = contactRepository.findByPhoneNumber(phoneNumber);
		friendContactOptional.orElseThrow(()-> new UsernameNotFoundException("Not Found: " + phoneNumber));
		Contact friend = friendContactOptional.get();
		
		Optional<Conversation> conversationOptional = userConversationRepository.findCommonConversationByContacts(contact.getContactId(), friend.getContactId());
		conversationOptional.orElseThrow(()-> new ConversationNotFoundException());
		Conversation conversation = conversationOptional.get();
		UserConversation myUserConversation = userConversationRepository.findById(new UserConversationId(contact.getContactId(), conversation.getConversationId())).get();
		UserConversation otherUserConversation = userConversationRepository.findById(new UserConversationId(friend.getContactId(), conversation.getConversationId())).get();
		
		myUserConversation.setStatus(ConversationStatus.LEFT);
		userConversationRepository.save(myUserConversation);
		if(otherUserConversation.getStatus() == ConversationStatus.LEFT) {
			messageRepository.deleteByConversation(conversation);
			userConversationRepository.deleteById(myUserConversation.getId());
			userConversationRepository.deleteById(otherUserConversation.getId());
			conversationRepository.deleteById(conversation.getConversationId());
		}else {
			Notification notification = new Notification();
	        notification.setType(NotificationType.REMOVED);
	        notification.setSenderId(contact.getContactId());
	        List<ContactDTO> contactDTOs = new ArrayList<>();
	        ContactDTO contactDTO = new ContactDTO(friend.getContactId(), friend.getUserName(), Base64.getEncoder().encodeToString(friend.getProfilePhoto()), friend.getStatus());
	        contactDTOs.add(contactDTO);
	        notification.setContacts(contactDTOs);
			notificationUtil.sendNotification(notification);
		}
	}
	
	public void blockFriend(String token, String phoneNumber) throws ConversationNotFoundException, InvalidRequestException {
		String contactId = jwtUtil.extractUsername(token);
		Optional<Contact> contactOptional = contactRepository.findById(Long.parseLong(contactId));
		contactOptional.orElseThrow(()-> new UsernameNotFoundException("Not Found: " + contactId));
		Contact contact = contactOptional.get();
		Optional<Contact> friendContactOptional = contactRepository.findByPhoneNumber(phoneNumber);
		friendContactOptional.orElseThrow(()-> new UsernameNotFoundException("Not Found: " + phoneNumber));
		Contact friend = friendContactOptional.get();
		
		Optional<Conversation> conversationOptional = userConversationRepository.findCommonConversationByContacts(contact.getContactId(), friend.getContactId());
		conversationOptional.orElseThrow(()-> new ConversationNotFoundException());
		Conversation conversation = conversationOptional.get();
		UserConversation myUserConversation = userConversationRepository.findById(new UserConversationId(contact.getContactId(), conversation.getConversationId())).get();
		UserConversation otherUserConversation = userConversationRepository.findById(new UserConversationId(friend.getContactId(), conversation.getConversationId())).get();
		
		
		if(otherUserConversation.getStatus() == ConversationStatus.BLOCKED) {
			throw new InvalidRequestException("Cannot be Blocked!");
		}
		myUserConversation.setStatus(ConversationStatus.BLOCKED);
		
		userConversationRepository.save(myUserConversation);
		
		
	}
	
	public void unBlockFriend(String token, String phoneNumber) throws ConversationNotFoundException, InvalidRequestException {
		String contactId = jwtUtil.extractUsername(token);
		Optional<Contact> contactOptional = contactRepository.findById(Long.parseLong(contactId));
		contactOptional.orElseThrow(()-> new UsernameNotFoundException("Not Found: " + contactId));
		Contact contact = contactOptional.get();
		Optional<Contact> friendContactOptional = contactRepository.findByPhoneNumber(phoneNumber);
		friendContactOptional.orElseThrow(()-> new UsernameNotFoundException("Not Found: " + phoneNumber));
		Contact friend = friendContactOptional.get();
		
		Optional<Conversation> conversationOptional = userConversationRepository.findCommonConversationByContacts(contact.getContactId(), friend.getContactId());
		conversationOptional.orElseThrow(()-> new ConversationNotFoundException());
		Conversation conversation = conversationOptional.get();
		UserConversation myUserConversation = userConversationRepository.findById(new UserConversationId(contact.getContactId(), conversation.getConversationId())).get();
		
		if(myUserConversation.getStatus() == ConversationStatus.BLOCKED) {
			myUserConversation.setStatus(ConversationStatus.ACTIVE);
			userConversationRepository.save(myUserConversation);
		}else {
			throw new InvalidRequestException("Cannot be UnBlocked!");
		}
	}
	
	public void muteFriend(String token, String phoneNumber) throws ConversationNotFoundException, InvalidRequestException {
		String contactId = jwtUtil.extractUsername(token);
		Optional<Contact> contactOptional = contactRepository.findById(Long.parseLong(contactId));
		contactOptional.orElseThrow(()-> new UsernameNotFoundException("Not Found: " + contactId));
		Contact contact = contactOptional.get();
		Optional<Contact> friendContactOptional = contactRepository.findByPhoneNumber(phoneNumber);
		friendContactOptional.orElseThrow(()-> new UsernameNotFoundException("Not Found: " + phoneNumber));
		Contact friend = friendContactOptional.get();
		
		Optional<Conversation> conversationOptional = userConversationRepository.findCommonConversationByContacts(contact.getContactId(), friend.getContactId());
		conversationOptional.orElseThrow(()-> new ConversationNotFoundException());
		Conversation conversation = conversationOptional.get();
		UserConversation myUserConversation = userConversationRepository.findById(new UserConversationId(contact.getContactId(), conversation.getConversationId())).get();
		
		if(myUserConversation.getStatus() == ConversationStatus.ACTIVE) {
			myUserConversation.setStatus(ConversationStatus.MUTE);
			userConversationRepository.save(myUserConversation);
		}else {
			throw new InvalidRequestException("Cannot be Muted!");
		}
	}
	
}
