package com.example.demo.dto;

import org.springframework.web.util.HtmlUtils;

import jakarta.validation.constraints.NotBlank;

public class SecurityAnswerDTO {
	@NotBlank(message = "Answer cannot be blank")
	private String answer;
	
	public SecurityAnswerDTO() {
		
	}

	public SecurityAnswerDTO(@NotBlank String answer) {
		super();
		this.answer = answer;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = sanitize(answer);
	}
	
	private String sanitize(String input) {
        // Perform HTML escaping to prevent XSS attacks
        return input != null ? HtmlUtils.htmlEscape(input.trim()) : null;
    }
}
