package com.example.demo.utils;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.example.demo.dto.Notification;
import com.example.demo.dto.NotificationType;

@Component
public class NotificationUtil {

	
	@Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

	
	public void sendNotification(Notification notification) {
		if(notification.getGroupName() == null || (notification.getGroupName() != null && 
				notification.getType() == NotificationType.ADDED && notification.getContact() != null)) {
			try {
			simpMessagingTemplate.convertAndSendToUser(Long.toString(notification.getContact().getcontactId()),"/notifications",notification);
			}
			catch(Exception e) {
				System.out.println(e);
			}
		}else {
			 simpMessagingTemplate.convertAndSend("/group/"+ notification.getConversationId(), notification);
		}
	}
	
//|| (notification.getGroupName() != null && notification.getContacts() != null && notification.getContacts().size() == 1)

}
