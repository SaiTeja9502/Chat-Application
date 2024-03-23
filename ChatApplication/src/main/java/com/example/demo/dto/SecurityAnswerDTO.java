package com.example.demo.dto;

import org.springframework.web.util.HtmlUtils;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class SecurityAnswerDTO {
	@NotBlank(message = "Answer cannot be blank")
	@Pattern(regexp = "^[a-zA-Z]+$", message = "Security Answer must contain only letters")
    @Size(max = 15, message = "Security Answer must not exceed 15 characters")
	private String answer;
	
	public SecurityAnswerDTO() {
		
	}

	public SecurityAnswerDTO(@NotBlank String answer) {
		super();
		this.answer = sanitize(answer);
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
