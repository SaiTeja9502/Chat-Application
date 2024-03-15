package com.example.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.demo.exceptions.AccountLockedException;
import com.example.demo.exceptions.AdminRemovalException;
import com.example.demo.exceptions.ConversationNotFoundException;
import com.example.demo.exceptions.InvalidSecurityAnswerException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
    	System.out.println("Handled");
        return ResponseEntity.badRequest().body("Validation error: " + ex.getMessage());
    }
    
    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<String> handleAccountLockedExceptions(AccountLockedException ex) {
    	System.out.println("Handled");
        return ResponseEntity.badRequest().body("Validation error: " + ex.getMessage());
    }
    
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> handleUsernameNotFoundExceptions(UsernameNotFoundException ex) {
    	System.out.println("Handled");
        return ResponseEntity.badRequest().body("Validation error: " + ex.getMessage());
    }
    
    @ExceptionHandler(InvalidSecurityAnswerException.class)
    public ResponseEntity<String> handleInvalidSecurityAnswerExceptions(InvalidSecurityAnswerException ex) {
    	System.out.println("Handled");
        return ResponseEntity.badRequest().body("Validation error: " + ex.getMessage());
    }
    
    @ExceptionHandler(AdminRemovalException.class)
    public ResponseEntity<String> handleAdminRemovalExceptions(AdminRemovalException ex) {
    	System.out.println("Handled");
        return ResponseEntity.badRequest().body("Validation error: " + ex.getMessage());
    }
    
    @ExceptionHandler(ConversationNotFoundException.class)
    public ResponseEntity<String> handleConversationNotFoundExceptions(ConversationNotFoundException ex) {
    	System.out.println("Handled");
        return ResponseEntity.badRequest().body("Validation error: " + ex.getMessage());
    }
}

