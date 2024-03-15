package com.example.demo.dto;

public class MessageFront {
	
	private Long senderId;
	private Long messageId;
	private String message;
	private Long conversationId;
	private MessageType type;
	
	public MessageFront() {
		
	}

	public MessageFront(Long senderId, Long messageId, String message, Long conversationId, String type) {
		super();
		this.senderId = senderId;
		this.messageId = messageId;
		this.message = message;
		this.conversationId = conversationId;
		this.type = MessageType.valueOf(type);
	}

	public Long getSenderId() {
		return senderId;
	}

	public void setSenderId(Long senderId) {
		this.senderId = senderId;
	}

	public Long getMessageId() {
		return messageId;
	}

	public void setMessageId(Long messageId) {
		this.messageId = messageId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Long getConversationId() {
		return conversationId;
	}

	public void setConversationId(Long conversationId) {
		this.conversationId = conversationId;
	}

	public MessageType getType() {
		return type;
	}

	public void setType(String type) {
		this.type = MessageType.valueOf(type);
	}
	
	

}
