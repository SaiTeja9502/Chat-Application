package com.example.demo.utils;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.example.demo.exceptions.ConversationNotFoundException;
import com.example.demo.model.Contact;
import com.example.demo.model.Conversation;
import com.example.demo.model.UserConversation;
import com.example.demo.model.UserConversationId;
import com.example.demo.repository.ContactRepository;
import com.example.demo.repository.ConversationRepository;
import com.example.demo.repository.UserConversationRepository;

@Component
public class Helper {
	
	@Autowired
	ContactRepository contactRepository;
	
	@Autowired
	ConversationRepository conversationRepository;
	
	@Autowired
	UserConversationRepository userConversationRepository;
	
	public String getUserRole(String contactId, String conversationId) {
		Optional<Contact> contactOptional = contactRepository.findById(Long.parseLong(contactId));
		contactOptional.orElseThrow(()-> new UsernameNotFoundException("Not Found: " + contactId));
		
		Optional<Conversation> conversationalOptional = conversationRepository.findById(Long.parseLong(conversationId));
		conversationalOptional.orElseThrow(()-> new ConversationNotFoundException());
		
		Optional<UserConversation> userConversationOptional = userConversationRepository.findById(new UserConversationId(contactOptional.get().getContactId(), conversationalOptional.get().getConversationId()));
		userConversationOptional.orElseThrow(()-> new ConversationNotFoundException());
		return userConversationOptional.get().getRole().toString();
		
	}
}
