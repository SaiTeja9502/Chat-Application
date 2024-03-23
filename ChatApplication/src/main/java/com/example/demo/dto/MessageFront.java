package com.example.demo.dto;

import org.springframework.web.util.HtmlUtils;

import jakarta.validation.constraints.NotBlank;

public class MessageFront {
	
	private String messageId;
	@NotBlank(message = "message is required")
	private String message;
	@NotBlank(message = "Conversation ID is required")
	private String conversationId;
	
	public MessageFront() {
		
	}

	public MessageFront(String messageId, String message, String conversationId) {
		super();
		this.messageId = sanitize(messageId);
		this.message = sanitize(message);
		this.conversationId = sanitize(conversationId);
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = sanitize(messageId);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = sanitize(message);
	}

	public String getConversationId() {
		return conversationId;
	}

	public void setConversationId(String conversationId) {
		this.conversationId = sanitize(conversationId);
	}
	
	private String sanitize(String input) {
        // Perform HTML escaping to prevent XSS attacks
        return input != null ? HtmlUtils.htmlEscape(input.trim()) : null;
    }

}
