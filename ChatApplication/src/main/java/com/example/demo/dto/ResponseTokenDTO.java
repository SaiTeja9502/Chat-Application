package com.example.demo.dto;

public class ResponseTokenDTO {
	private Long userId;
	private String username;
	private String base64Image;
    private String accessToken;
    private String token;

    // Default constructor
    public ResponseTokenDTO() {
    }

    // Parameterized constructor
    public ResponseTokenDTO(Long userId, String username, String base64Image, String accessToken, String token) {
        
    	this.userId = userId;
    	this.username = username;
    	this.base64Image = base64Image;
    	this.accessToken = accessToken;
        this.token = token;
    }

    public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
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

	// Getter and setter methods for accessToken
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    // Getter and setter methods for token
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

