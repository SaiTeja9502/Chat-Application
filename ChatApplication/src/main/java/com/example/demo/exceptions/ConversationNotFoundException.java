package com.example.demo.exceptions;

public class ConversationNotFoundException extends RuntimeException{
	public ConversationNotFoundException() {
		super("Cannot find Conversation!");
	}
}
