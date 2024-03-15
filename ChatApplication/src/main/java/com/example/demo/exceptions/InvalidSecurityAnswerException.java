package com.example.demo.exceptions;

public class InvalidSecurityAnswerException extends RuntimeException {
    public InvalidSecurityAnswerException() {
        super("Security answer is incorrect!");
    }
}

