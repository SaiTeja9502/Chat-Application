package com.example.demo.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.GroupCreateDTO;
import com.example.demo.exceptions.AdminRemovalException;
import com.example.demo.exceptions.ConversationNotFoundException;
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
	
	public void CreateNewGroup(String token, GroupCreateDTO groupCreateDTO) {
		String contactId = jwtUtil.extractUsername(token);
		Optional<Contact> contactOptional = contactRepository.findById(Long.parseLong(contactId));
		contactOptional.orElseThrow(()-> new UsernameNotFoundException("Not Found: " + contactId));
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
	}

	public void makeAsAdmin(String token, Long conversationId, Long Id) throws ConversationNotFoundException {
		
		Optional<Contact> memberOptional = contactRepository.findById(Id);
		memberOptional.orElseThrow(()-> new UsernameNotFoundException("Not Found: " + Id));
		
		Optional<Conversation> conversationOptional = conversationRepository.findById(conversationId);
		conversationOptional.orElseThrow(()-> new ConversationNotFoundException());
		
		UserConversation userConversation = userConversationRepository.findById(new UserConversationId(Id, conversationId)).get();
		
		if(userConversation.getRole() == UserRole.USER) {
			userConversation.setRole(UserRole.ADMIN);
			userConversationRepository.save(userConversation);
		}
	}

	public void removeUser(Long conversationId, Long contactId) {
		
		Optional<Contact> memberOptional = contactRepository.findById(contactId);
		memberOptional.orElseThrow(()-> new UsernameNotFoundException("Not Found: " + contactId));
		Contact contact = memberOptional.get();
		
		Optional<Conversation> conversationOptional = conversationRepository.findById(conversationId);
		conversationOptional.orElseThrow(()-> new ConversationNotFoundException());
		Conversation conversation = conversationOptional.get();
		
		UserConversation userConversation = userConversationRepository.findById(new UserConversationId(contactId, conversationId)).get();
		
		if(userConversation.getRole() == UserRole.ADMIN) {
			throw new AdminRemovalException();
		}
		
	    List<Message> messages = messageRepository.findByContactAndConversation(contact, conversation);
	    messageRepository.deleteAll(messages);

	    for (Message message : messages) {
	        userMessageStatusRepository.deleteById(new UserMessageStatusId(contactId, message.getMessageId()));
	    }
		
	}

	public void addUser(Long conversationId, Long contactId) {
		
		Optional<Contact> memberOptional = contactRepository.findById(contactId);
		memberOptional.orElseThrow(()-> new UsernameNotFoundException("Not Found: " + contactId));
		Contact contact = memberOptional.get();
		
		Optional<Conversation> conversationOptional = conversationRepository.findById(conversationId);
		conversationOptional.orElseThrow(()-> new ConversationNotFoundException());
		Conversation conversation = conversationOptional.get();
		
		UserConversation userConversation = new UserConversation(new UserConversationId(contactId, conversationId),
				contact, conversation, LocalDateTime.now(), null, ConversationStatus.ACTIVE, UserRole.USER);
		
		userConversationRepository.save(userConversation);
	}

	public void leaveGroup(Long conversationId, String token) {
		String contactId = jwtUtil.extractUsername(token);
		Optional<Contact> contactOptional = contactRepository.findById(Long.parseLong(contactId));
		removeUser(conversationId, contactOptional.get().getContactId());
	}

	public void mute(Long conversationId, String token) {
		String contactId = jwtUtil.extractUsername(token);
		Optional<Contact> contactOptional = contactRepository.findById(Long.parseLong(contactId));
		contactOptional.orElseThrow(()-> new UsernameNotFoundException("Not Found: " + contactId));
		Contact contact = contactOptional.get();
		
		Optional<Conversation> conversationOptional = conversationRepository.findById(conversationId);
		conversationOptional.orElseThrow(()-> new ConversationNotFoundException());
		Conversation conversation = conversationOptional.get();
		
		UserConversation userConversation = userConversationRepository.findById(new UserConversationId(contact.getContactId(), conversationId)).get();	
		
		userConversation.setStatus(ConversationStatus.MUTE);
	}

	public void updatePhoto(String token, MultipartFile file, Long conversationId) throws IOException {
		String contactId = jwtUtil.extractUsername(token);
		Optional<Contact> contactOptional = contactRepository.findById(Long.parseLong(contactId));
		contactOptional.orElseThrow(()-> new UsernameNotFoundException("Not Found: " + contactId));
		Contact contact = contactOptional.get();
		
		Optional<Conversation> conversationOptional = conversationRepository.findById(conversationId);
		conversationOptional.orElseThrow(()-> new ConversationNotFoundException());
		Conversation conversation = conversationOptional.get();
		
		Optional<UserConversation> userConversation = userConversationRepository.findById(new UserConversationId(contact.getContactId(), conversationId));
		userConversation.orElseThrow(()-> new ConversationNotFoundException());
		
		conversation.setProfilePhoto(file.getBytes());
		conversationRepository.save(conversation);
	}
	
	
}
