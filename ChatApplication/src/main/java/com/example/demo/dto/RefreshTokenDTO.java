package com.example.demo.dto;

import org.springframework.web.util.HtmlUtils;

import jakarta.validation.constraints.NotBlank;

public class RefreshTokenDTO {
	
	@NotBlank(message = "Refresh token must not be blank")
	private String refreshToken;
	
	public RefreshTokenDTO() {
		
	}

	public RefreshTokenDTO(String refreshToken) {
		super();
		this.refreshToken = sanitize(refreshToken);
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = sanitize(refreshToken);
	}
	
	private String sanitize(String input) {
        // Perform HTML escaping to prevent XSS attacks
        return input != null ? HtmlUtils.htmlEscape(input.trim()) : null;
    }
}
