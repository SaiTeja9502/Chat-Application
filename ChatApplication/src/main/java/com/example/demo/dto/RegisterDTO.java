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
	@Size(max = 20, message = "Username cannot be more than 20 characters")
	private String userName;
	@NotBlank(message = "Password is required")
    @Size(min = 8, max = 20, message = "Password must be at least 8 characters")
	private String password;
	@NotBlank(message = "Password is required")
	@Size(min = 8, max = 20, message = "Password must be at least 8 characters")
	private String confirmPassword;
	@NotBlank(message = "Security Question is required")
	private String securityQuestion;
	@NotBlank(message = "Security Answer is required")
	@Pattern(regexp = "^[a-zA-Z]+$", message = "Security Answer must contain only letters")
    @Size(max = 15, message = "Security Answer must not exceed 15 characters")
	private String securityAnswer;
	
	public RegisterDTO() {
		
	}

	public RegisterDTO(String phoneNumber, String userName, String password, String confirmPassword,
			String securityQuestion, String securityAnswer) {
		super();
		this.phoneNumber = sanitize(phoneNumber);
		this.userName = sanitize(userName);
		this.password = sanitize(password);
		this.confirmPassword = sanitize(confirmPassword);
		this.securityQuestion = sanitize(securityQuestion);
		this.securityAnswer = sanitize(securityAnswer);
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

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
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
	    if (input == null) {
	        return null;
	    }

	    // Perform HTML escaping, excluding the apostrophe
	    StringBuilder sanitizedInput = new StringBuilder();
	    for (char c : input.toCharArray()) {
	        if (c == '\'') {
	            sanitizedInput.append(c); // Preserve apostrophe as is
	        } else {
	            sanitizedInput.append(HtmlUtils.htmlEscape(Character.toString(c)));
	        }
	    }
	    return sanitizedInput.toString();
	}

	
}
