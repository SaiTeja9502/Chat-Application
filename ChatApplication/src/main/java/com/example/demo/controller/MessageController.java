package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ChatDTO;
import com.example.demo.dto.MessageFront;
import com.example.demo.dto.Notification;
import com.example.demo.model.Message;
import com.example.demo.service.MessageService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class MessageController {
	
	
	@Autowired
	MessageService messageService;
	
	@Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
	
	@GetMapping("/messages/{offset}")  
	public ResponseEntity<?> getMessages(HttpServletRequest request,@PathVariable int offset) {
		String authorizationHeader = request.getHeader("Authorization");
        String token = authorizationHeader.substring(7);
        try {
			return new ResponseEntity<List<ChatDTO>>(messageService.getAllMessages(token, offset), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>("Failed to Logout", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@MessageMapping("/private/{senderId}")
    public void privateMessage(@Payload MessageFront message){
		Long receiverId = messageService.getReceiverId(message.getSenderId(), message.getConversationId()); 
		Notification notification = messageService.processMessage(message);
		if(receiverId != null && notification != null) {
			simpMessagingTemplate.convertAndSendToUser( String.valueOf(receiverId) ,"/notifications",notification);
		}
    }
	
	@MessageMapping("/group/{groupId}")
	@SendTo("/group/{groupId}")
    public Notification groupMessage(@Payload MessageFront message){
		return messageService.processMessage(message);
    }
	
	
	
}
