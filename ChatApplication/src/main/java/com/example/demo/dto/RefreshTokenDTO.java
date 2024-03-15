package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;

public class RefreshTokenDTO {
	
	@NotBlank(message = "Refresh token must not be blank")
	private String refreshToken;
	
	public RefreshTokenDTO() {
		
	}

	public RefreshTokenDTO(String refreshToken) {
		super();
		this.refreshToken = refreshToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	
	
}
