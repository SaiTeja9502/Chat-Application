package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ChatDTO;
import com.example.demo.dto.MessageDTO;
import com.example.demo.dto.MessageFront;
import com.example.demo.dto.Notification;
import com.example.demo.model.Message;
import com.example.demo.service.MessageService;
import com.example.demo.utils.SanitizationUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/messages")
@CrossOrigin
public class MessageController {
	
	
	@Autowired
	MessageService messageService;
	
	@GetMapping("/conversations")  
	public ResponseEntity<?> getConversations(HttpServletRequest request) {
		String authorizationHeader = request.getHeader("Authorization");
        String token = authorizationHeader.substring(7);
        try {
			return new ResponseEntity<List<ChatDTO>>(messageService.getConversations(token), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>("Failed!", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/conversation/{conversationid}")  
	public ResponseEntity<?> getConversation(HttpServletRequest request, @PathVariable String conversationid) {
		String authorizationHeader = request.getHeader("Authorization");
        String token = authorizationHeader.substring(7);
        Long conversationId = Long.parseLong(SanitizationUtils.sanitizeHtml(conversationid));
        try {
			return new ResponseEntity<ChatDTO>(messageService.getConversation(token, conversationId), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>("Failed!", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@GetMapping("/{conversationId}/{offset}")  
	public ResponseEntity<?> getMessages(HttpServletRequest request,@PathVariable Long conversationId,@PathVariable int offset) {
		String authorizationHeader = request.getHeader("Authorization");
        String token = authorizationHeader.substring(7);
        try {
			return new ResponseEntity<List<MessageDTO>>(messageService.getMessages(token, conversationId, offset), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>("Failed!", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@PostMapping 
	public ResponseEntity<?> sendMessage(HttpServletRequest request, @Valid @RequestBody MessageFront message) {
		String authorizationHeader = request.getHeader("Authorization");
        String token = authorizationHeader.substring(7);
        try {
			return new ResponseEntity<MessageDTO>(messageService.sendMessage(token, message), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>("Failed!", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DeleteMapping  
	public ResponseEntity<?> deleteMessage(HttpServletRequest request, @RequestBody MessageFront message) {
		String authorizationHeader = request.getHeader("Authorization");
        String token = authorizationHeader.substring(7);
        try {
        	messageService.deleteMessage(token, message);
			return new ResponseEntity<String>("Successful", HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>("Failed!", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PutMapping("/{conversationid}")
	public ResponseEntity<?> readMessage(HttpServletRequest request, @PathVariable String conversationid) {
		String authorizationHeader = request.getHeader("Authorization");
        String token = authorizationHeader.substring(7);
        Long conversationId = Long.parseLong(SanitizationUtils.sanitizeHtml(conversationid));
        try {
        	messageService.readMessage(token, conversationId);
			return new ResponseEntity<String>("Successful", HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>("Failed!", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	
}
