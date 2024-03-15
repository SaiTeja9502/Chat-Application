package com.example.demo.dto;

import org.springframework.web.util.HtmlUtils;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ResetPasswordDTO {
	@NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
	private String password;
	public ResetPasswordDTO() {
		
	}
	public ResetPasswordDTO(String password) {
		super();
		this.password = password;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = sanitize(password);
	}
	
	private String sanitize(String input) {
        // Perform HTML escaping to prevent XSS attacks
        return input != null ? HtmlUtils.htmlEscape(input.trim()) : null;
    }
	
}
