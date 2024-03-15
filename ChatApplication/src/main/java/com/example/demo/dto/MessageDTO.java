package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.List;

public class MessageDTO {
	
	private Long senderId;
	private Long messageId;
	private String message;
	private LocalDateTime sentDateTime;
	private boolean isRead;
	private LocalDateTime readDateTime;
	private List<UserStatusDTO> isReadInfo;
	
	public MessageDTO() {
		
	}

	public MessageDTO(Long senderId, Long messageId, String message, LocalDateTime sentDateTime, boolean isRead, LocalDateTime readDateTime, List<UserStatusDTO> isReadInfo) {
		super();
		this.senderId = senderId;
		this.messageId = messageId;
		this.message = message;
		this.sentDateTime = sentDateTime;
		this.isRead = isRead;
		this.readDateTime = readDateTime;
		this.isReadInfo = isReadInfo;
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
	
	

	public LocalDateTime getSentDateTime() {
		return sentDateTime;
	}

	public void setSentDateTime(LocalDateTime sentDateTime) {
		this.sentDateTime = sentDateTime;
	}

	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}
	
	public LocalDateTime getReadDateTime() {
		return readDateTime;
	}

	public void setReadDateTime(LocalDateTime readDateTime) {
		this.readDateTime = readDateTime;
	}

	public List<UserStatusDTO> getIsReadInfo() {
		return isReadInfo;
	}

	public void setIsReadInfo(List<UserStatusDTO> isReadInfo) {
		this.isReadInfo = isReadInfo;
	}
	
	

}
