package com.example.demo.exceptions;

public class AdminRemovalException extends RuntimeException {
	public AdminRemovalException() {
		super("Admin cannot be removed!");
	}
}
