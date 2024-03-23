package com.example.demo.dto;

import org.springframework.web.util.HtmlUtils;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class FriendDTO {

	@NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be 10 digits")
	private String phoneNumber;
	
	public FriendDTO() {
		
	}

	public FriendDTO(String phoneNumber) {
		super();
		this.phoneNumber = sanitize(phoneNumber);
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = sanitize(phoneNumber);
	}
	
	private String sanitize(String input) {
        // Perform HTML escaping to prevent XSS attacks
        return input != null ? HtmlUtils.htmlEscape(input.trim()) : null;
    }
}
