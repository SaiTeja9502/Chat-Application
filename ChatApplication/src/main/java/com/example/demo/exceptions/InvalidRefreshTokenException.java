package com.example.demo.exceptions;

public class InvalidRefreshTokenException extends RuntimeException{
	public InvalidRefreshTokenException() {
		super("Refresh token is invalid!");
	}
	
	public InvalidRefreshTokenException(String msg) {
		super(msg);
	}
}
