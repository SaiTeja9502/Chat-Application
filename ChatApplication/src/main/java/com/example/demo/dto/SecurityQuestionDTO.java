package com.example.demo.dto;


public class SecurityQuestionDTO {
	private String securityQuestion;
	private String token;
	
	public SecurityQuestionDTO() {
		
	}

	public SecurityQuestionDTO(String securityQuestion, String token) {
		super();
		this.securityQuestion = securityQuestion;
		this.token = token;
	}

	public String getSecurityQuestion() {
		return securityQuestion;
	}

	public void setSecurityQuestion(String securityQuestion) {
		this.securityQuestion = securityQuestion;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
