package com.example.demo.dto;

import java.time.LocalDateTime;

public class UserStatusDTO {
	private Long userId;
	private boolean isRead;
	private LocalDateTime readDateTime;
	
	public UserStatusDTO() {
		
	}

	public UserStatusDTO(Long userId, boolean isRead, LocalDateTime readDateTime) {
		super();
		this.userId = userId;
		this.isRead = isRead;
		this.readDateTime = readDateTime;
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

	public LocalDateTime getReadDateTime() {
		return readDateTime;
	}

	public void setReadDateTime(LocalDateTime readDateTime) {
		this.readDateTime = readDateTime;
	}
	
	
}
