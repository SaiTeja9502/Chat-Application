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
import com.example.demo.dto.GroupCreateDTO;
import com.example.demo.dto.Notification;
import com.example.demo.dto.NotificationType;
import com.example.demo.exceptions.AdminRemovalException;
import com.example.demo.exceptions.ConversationNotFoundException;
import com.example.demo.exceptions.NoPermissionException;
import com.example.demo.model.Contact;
import com.example.demo.model.Conversation;
import com.example.demo.model.ConversationStatus;
import com.example.demo.model.Message;
import com.example.demo.model.UserConversation;
import com.example.demo.model.UserConversationId;
import com.example.demo.model.UserMessageStatusId;
import com.example.demo.model.UserRole;
import com.example.demo.repository.ContactRepository;
import com.example.demo.repository.ConversationRepository;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.UserConversationRepository;
import com.example.demo.repository.UserMessageStatusRepository;
import com.example.demo.utils.JWTUtil;
import com.example.demo.utils.NotificationUtil;

@Service
public class GroupService {
	
	@Autowired
	ContactRepository contactRepository;
	
	@Autowired
	ConversationRepository conversationRepository;
	
	@Autowired
	UserConversationRepository userConversationRepository;
	
	@Autowired
	MessageRepository messageRepository;
	
	@Autowired
	UserMessageStatusRepository userMessageStatusRepository;
	
	@Autowired
	JWTUtil jwtUtil;
	
	@Autowired
	NotificationUtil notificationUtil;

	
	public void CreateNewGroup(String token, GroupCreateDTO groupCreateDTO) {
		String contactId = jwtUtil.extractUsername(token);
		Optional<Contact> contactOptional = contactRepository.findById(Long.parseLong(contactId));
		Contact contact = contactOptional.get();
		
		Conversation conversation = new Conversation(groupCreateDTO.getGroupName(), null, LocalDateTime.now());
		
		Conversation savedConversation = conversationRepository.save(conversation);
		
		List<Contact> contacts = contactRepository.findByContactIdIn(groupCreateDTO.getContactIds());
		
		List<UserConversation> userConversations = new ArrayList<>();
		for(Contact con : contacts) {
			UserConversation userconversation = new UserConversation(new UserConversationId(con.getContactId(), savedConversation.getConversationId()),
					con, savedConversation, LocalDateTime.now(), null, ConversationStatus.ACTIVE, UserRole.USER);
			userConversations.add(userconversation);
		}
		
		userConversations.add(new UserConversation(new UserConversationId(contact.getContactId(), savedConversation.getConversationId()),
				contact, savedConversation, LocalDateTime.now(), null, ConversationStatus.ACTIVE, UserRole.ADMIN));
		
		userConversationRepository.saveAll(userConversations);
		
		for(Contact con : contacts) {
			Notification notification = new Notification();
	        notification.setType(NotificationType.ADDED);
	        notification.setConversationId(savedConversation.getConversationId());
	        ContactDTO contactDTO = new ContactDTO(con.getContactId(), con.getUserName(), null, con.getStatus(), null);
        	notification.setContact(contactDTO);
	        
			notificationUtil.sendNotification(notification);
		}
	}

	public void makeAsAdmin(String token, Long conversationId, Long Id) throws NoPermissionException {
		String contactId = jwtUtil.extractUsername(token);
		Optional<Contact> adminOptional = contactRepository.findById(Long.parseLong(contactId));
		
		Optional<Contact> memberOptional = contactRepository.findById(Id);
		memberOptional.orElseThrow(()-> new UsernameNotFoundException("Not Found: " + Id));
		Contact con = memberOptional.get();
		Optional<UserConversation> userConversationOptional = userConversationRepository.findById(new UserConversationId(Id, conversationId));
		userConversationOptional.orElseThrow(()-> new NoPermissionException("Not a part of the conversation"));
		UserConversation userConversation = userConversationOptional.get();
		
		
		if(userConversation.getRole() == UserRole.USER) {
			userConversation.setRole(UserRole.ADMIN);
			userConversationRepository.save(userConversation);
			Notification notification = new Notification();
	        notification.setType(NotificationType.ADMIN);
	        notification.setGroupName(userConversation.getConversation().getConversationName());
	        notification.setSenderId(adminOptional.get().getContactId());
	        notification.setConversationId(conversationId);
	        ContactDTO contactDTO = new ContactDTO(con.getContactId(), con.getUserName(), null, con.getStatus(), null);
        	notification.setContact(contactDTO);
			notificationUtil.sendNotification(notification);	
		}
		
		
	}

	public void removeUser(String token, Long conversationId, Long contactId, boolean left) throws NoPermissionException{
		
		String adminId = jwtUtil.extractUsername(token);
		Optional<Contact> adminOptional = contactRepository.findById(Long.parseLong(adminId));
		
		Optional<Contact> memberOptional = contactRepository.findById(contactId);
		memberOptional.orElseThrow(()-> new UsernameNotFoundException("Not Found: " + contactId));
		Contact member = memberOptional.get();
		
		Optional<UserConversation> userConversationOptional = userConversationRepository.findById(new UserConversationId(contactId, conversationId));
		userConversationOptional.orElseThrow(()-> new NoPermissionException("Not a part of the conversation"));
		UserConversation userConversation = userConversationOptional.get();
		
		if(userConversation.getRole() == UserRole.ADMIN && adminOptional.get().getContactId()!= member.getContactId()) {
			throw new AdminRemovalException();
		}
		
	    List<Message> messages = messageRepository.findByContactAndConversation(member, userConversation.getConversation());
	    
	    for (Message message : messages) {
	        userMessageStatusRepository.deleteById(new UserMessageStatusId(contactId, message.getMessageId()));
	    }
	    
	    messageRepository.deleteAll(messages);
	    
	    userConversationRepository.delete(userConversation);

	    Notification notification = new Notification();
        notification.setType(left ? NotificationType.LEFT : NotificationType.REMOVED);
        notification.setGroupName(userConversation.getConversation().getConversationName());
        notification.setSenderId(adminOptional.get().getContactId());
        notification.setConversationId(conversationId);
        ContactDTO contactDTO = new ContactDTO(member.getContactId(), member.getUserName(), null, member.getStatus(), null);
    	notification.setContact(contactDTO);
		notificationUtil.sendNotification(notification);	
		
	}

	public void addUser(String token, Long conversationId, Long contactId) {
		
		String adminId = jwtUtil.extractUsername(token);
		
		Optional<Conversation> conversationOptional = conversationRepository.findById(conversationId);
		conversationOptional.orElseThrow(()-> new ConversationNotFoundException());
		Conversation conversation = conversationOptional.get();
		
		Optional<Contact> memberOptional = contactRepository.findById(contactId);
		memberOptional.orElseThrow(()-> new UsernameNotFoundException("Not Found: " + contactId));
		Contact contact = memberOptional.get();
		
		Optional<UserConversation> memberConversationOptional = userConversationRepository.findById(new UserConversationId(contact.getContactId(), conversationId));
		
		if(memberConversationOptional.isPresent()) {
			throw new NoPermissionException("Already Present!");
		}
		
		UserConversation userConversation = new UserConversation(new UserConversationId(contactId, conversationId),
				contact, conversation, LocalDateTime.now(), null, ConversationStatus.ACTIVE, UserRole.USER);
		
		userConversationRepository.save(userConversation);
		
		
		Notification notification = new Notification();
        notification.setType(NotificationType.ADDED);
        notification.setSenderId(Long.parseLong(adminId));
        notification.setConversationId(conversationId);
        notification.setGroupName(conversation.getConversationName());
        
        notificationUtil.sendNotification(notification);

        ContactDTO contactDTO = new ContactDTO(contact.getContactId(), contact.getUserName(), null, contact.getStatus(), null);
    	notification.setContact(contactDTO);
    	
    	notificationUtil.sendNotification(notification);
    	
	}

	public void leaveGroup(Long conversationId, String token) {
		String contactId = jwtUtil.extractUsername(token);
		Optional<Contact> contactOptional = contactRepository.findById(Long.parseLong(contactId));
		removeUser(token, conversationId, contactOptional.get().getContactId(), true);
	}

	public void mute(Long conversationId, String token) {
		String contactId = jwtUtil.extractUsername(token);
		
		UserConversation userConversation = userConversationRepository.findById(new UserConversationId(Long.parseLong(contactId), conversationId)).get();	
		
		userConversation.setStatus(ConversationStatus.MUTE);
		
		userConversationRepository.save(userConversation);
	}

	public void updatePhoto(String token, MultipartFile file, Long conversationId) throws IOException {
		String contactId = jwtUtil.extractUsername(token);
		
		Optional<Conversation> conversationOptional = conversationRepository.findById(conversationId);
		Conversation conversation = conversationOptional.get();
		
		Optional<UserConversation> userConversation = userConversationRepository.findById(new UserConversationId(Long.parseLong(contactId), conversationId));
		userConversation.orElseThrow(()-> new ConversationNotFoundException());

		conversation.setProfilePhoto(file.getBytes());
		conversationRepository.save(conversation);
	}
	
}
