package com.example.demo.exceptions;

public class AccountLockedException extends RuntimeException {

    public AccountLockedException() {
        super("Account is locked.");
    }

    public AccountLockedException(String message) {
        super(message);
    }

    public AccountLockedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccountLockedException(Throwable cause) {
        super("Account is locked.", cause);
    }
}

