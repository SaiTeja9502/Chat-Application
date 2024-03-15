package com.example.demo.dto;

import org.springframework.web.util.HtmlUtils;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterDTO {
	@NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be 10 digits")
	private String phoneNumber;
	@NotBlank(message = "Username is required")
	private String userName;
	@NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
	private String password;
	@NotBlank(message = "Security Question is required")
	private String securityQuestion;
	@NotBlank(message = "Security Answer is required")
	@Pattern(regexp = "^[a-zA-Z]+$", message = "Security Answer must contain only letters")
    @Size(max = 15, message = "Security Answer must not exceed 15 characters")
	private String securityAnswer;
	
	public RegisterDTO() {
		
	}

	public RegisterDTO(String phoneNumber, String userName, String password,
			String securityQuestion, String securityAnswer) {
		super();
		this.phoneNumber = phoneNumber;
		this.userName = userName;
		this.password = password;
		this.securityQuestion = securityQuestion;
		this.securityAnswer = securityAnswer;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = sanitize(phoneNumber);
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = sanitize(password);
	}

	public String getSecurityQuestion() {
		return securityQuestion;
	}

	public void setSecurityQuestion(String securityQuestion) {
		this.securityQuestion = sanitize(securityQuestion);
	}

	public String getSecurityAnswer() {
		return securityAnswer;
	}

	public void setSecurityAnswer(String securityAnswer) {
		this.securityAnswer = sanitize(securityAnswer);
	}
	
	private String sanitize(String input) {
        // Perform HTML escaping to prevent XSS attacks
        return input != null ? HtmlUtils.htmlEscape(input.trim()) : null;
    }
	
}
