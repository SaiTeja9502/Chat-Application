package com.example.demo.dto;

public class ResponseTokenDTO {
    private String accessToken;
    private String token;

    // Default constructor
    public ResponseTokenDTO() {
    }

    // Parameterized constructor
    public ResponseTokenDTO(String accessToken, String token) {
        this.accessToken = accessToken;
        this.token = token;
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

