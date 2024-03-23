package com.example.demo.dto;


public class UserStatusDTO {
	private Long userId;
	private boolean isRead;
	private String readDateTime;
	private boolean isDelivered;
	private String deliveredDateTime;
	
	public UserStatusDTO() {
		
	}

	public UserStatusDTO(Long userId, boolean isRead, String readDateTime, boolean isDelivered, String deliveredDateTime) {
		super();
		this.userId = userId;
		this.isRead = isRead;
		this.readDateTime = readDateTime;
		this.isDelivered = isDelivered;
		this.deliveredDateTime = deliveredDateTime;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
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
	
	
	
	
}
