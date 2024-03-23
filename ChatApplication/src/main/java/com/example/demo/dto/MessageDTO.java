package com.example.demo.dto;

import java.util.List;

public class MessageDTO {
	
	private Long senderId;
	private Long messageId;
	private String message;
	private String sentDateTime;
	private boolean isRead;
	private String readDateTime;
	private boolean isDelivered;
	private String deliveredDateTime;
	private List<UserStatusDTO> isReadInfo;
	
	public MessageDTO() {
		
	}

	public MessageDTO(Long senderId, Long messageId, String message, String sentDateTime, boolean isRead,
			String readDateTime, boolean isDelivered,  String deliveredDateTime ,List<UserStatusDTO> isReadInfo) {
		super();
		this.senderId = senderId;
		this.messageId = messageId;
		this.message = message;
		this.sentDateTime = sentDateTime;
		this.isRead = isRead;
		this.readDateTime = readDateTime;
		this.isDelivered = isDelivered;
		this.deliveredDateTime = deliveredDateTime;
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
	
	

	public String getSentDateTime() {
		return sentDateTime;
	}

	public void setSentDateTime(String sentDateTime) {
		this.sentDateTime = sentDateTime;
	}

	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}
	
	public String getReadDateTime() {
		return readDateTime;
	}

	public void setReadDateTime(String readDateTime) {
		this.readDateTime = readDateTime;
	}

	public List<UserStatusDTO> getIsReadInfo() {
		return isReadInfo;
	}
	
	

	public boolean isDelivered() {
		return isDelivered;
	}

	public void setDelivered(boolean isDelivered) {
		this.isDelivered = isDelivered;
	}

	public String getDeliveredDateTime() {
		return deliveredDateTime;
	}

	public void setDeliveredDateTime(String deliveredDateTime) {
		this.deliveredDateTime = deliveredDateTime;
	}

	public void setIsReadInfo(List<UserStatusDTO> isReadInfo) {
		this.isReadInfo = isReadInfo;
	}
	
	

}
