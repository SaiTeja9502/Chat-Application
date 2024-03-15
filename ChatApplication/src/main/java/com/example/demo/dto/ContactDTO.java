package com.example.demo.dto;

import com.example.demo.model.UserStatus;

public class ContactDTO {
	
	private Long contactId;
	private String username;
	private String base64Image;
	private UserStatus status;
	
	public ContactDTO() {
		
	}

	public ContactDTO(Long contactId, String username, String base64Image, UserStatus status) {
		super();
		this.contactId = contactId;
		this.username = username;
		this.base64Image = base64Image;
		this.status = status;
	}
	
	public Long getcontactId() {
		return contactId;
	}

	public void setcontactId(Long contactId) {
		this.contactId = contactId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getBase64Image() {
		return base64Image;
	}

	public void setBase64Image(String base64Image) {
		this.base64Image = base64Image;
	}

	public UserStatus getStatus() {
		return status;
	}

	public void setStatus(UserStatus status) {
		this.status = status;
	}
	
	

}
