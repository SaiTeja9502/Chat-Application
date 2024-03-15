package com.example.demo.utils;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.example.demo.dto.Notification;
import com.example.demo.dto.NotificationType;
import com.example.demo.model.Contact;
import com.example.demo.model.Conversation;
import com.example.demo.model.UserConversation;
import com.example.demo.repository.ContactRepository;
import com.example.demo.repository.UserConversationRepository;

public class NotificationUtil {
	
	@Autowired
	ContactRepository contactRepository;

	@Autowired
	UserConversationRepository userConversationRepository;
	
	@Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
	
	public void sendNotifications(Contact contact, Notification notification) {
		List<UserConversation> userConversations = userConversationRepository.findAllByContact(contact);

        for (UserConversation userConversation : userConversations) {
            Conversation conversation = userConversation.getConversation();
            boolean isGroupConversation = conversation.getConversationName() != null; // Assuming group conversation has a name
            notification.setConversationId(conversation.getConversationId());
            if (isGroupConversation) {
                simpMessagingTemplate.convertAndSend("/group/"+ conversation.getConversationId(), notification);
                
            } else {
                // For individual conversation, get the other contact involved
                Contact otherContact = getReceiverId(contact, conversation);
                if (otherContact != null) {
                	simpMessagingTemplate.convertAndSendToUser(Long.toString(otherContact.getContactId()) ,"/notifications",notification);
                }
            }
        }
		
	}
	
	public void sendNotification(Notification notification) {
		if(notification.getGroupName().isEmpty()) {
			simpMessagingTemplate.convertAndSendToUser(Long.toString(notification.getContacts().get(0).getcontactId()),"/notifications",notification);
		}else {
			 simpMessagingTemplate.convertAndSend("/group/"+ notification.getConversationId(), notification);
		}
	}
	
	public Contact getReceiverId(Contact contact, Conversation conversation) {
		
		List<Contact> contacts =  userConversationRepository.findAllByConversation(conversation);
	
		for(Contact con: contacts) {
			if(con.getContactId() != con.getContactId()) {
				return con;
			}
		}
		return null;
	}
}
