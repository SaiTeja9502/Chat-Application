package com.example.demo.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import com.example.demo.dto.RefreshTokenDTO;
import com.example.demo.dto.RegisterDTO;
import com.example.demo.dto.ResetPasswordDTO;
import com.example.demo.dto.ResetTokenDTO;
import com.example.demo.dto.ResponseTokenDTO;
import com.example.demo.dto.SecurityAnswerDTO;
import com.example.demo.dto.SecurityQuestionDTO;
import com.example.demo.dto.UserLogin;
import com.example.demo.model.Contact;
import com.example.demo.repository.ContactRepository;
import com.example.demo.service.AccountService;
import com.example.demo.utils.SanitizationUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/account")
@CrossOrigin
public class AccountController {
	
	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	AccountService accountService;
	
	@Autowired
	ContactRepository contactRepository;
	
	@PostMapping("/login")  //exposed
	public ResponseEntity<?> login(@Valid @RequestBody UserLogin userLogin) {
		String phoneNumber = SanitizationUtils.sanitizeHtml(userLogin.getPhoneNumber());
		String password = SanitizationUtils.sanitizeHtml(userLogin.getPassword());
		try {
			Optional<Contact> contact = contactRepository.findByPhoneNumber(phoneNumber);
			if(contact.isEmpty()) {
				throw new UsernameNotFoundException("invalid user request..!!");
			}
			Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(contact.get().getContactId(), password));
			if(authentication.isAuthenticated()) {
				return new ResponseEntity<ResponseTokenDTO>(accountService.createTokenResponseDTO(phoneNumber), HttpStatus.ACCEPTED);
			}
			else {
				throw new UsernameNotFoundException("invalid user request..!!");
			}
		}
		catch(Exception e) {
			return new ResponseEntity<String>("Invalid Phonenumber or password", HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/logout")  
	public ResponseEntity<String> logout(HttpServletRequest request) {
		String authorizationHeader = request.getHeader("Authorization");
        String token = authorizationHeader.substring(7);
        try {
			accountService.logOutUser(token);
			return new ResponseEntity<String>("Loggedout Successfully", HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>("Failed to Logout", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/refreshToken")  //exposed
	public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenDTO refreshToken){
		String refToken = SanitizationUtils.sanitizeHtml(refreshToken.getRefreshToken());
	    try {
	    	return new ResponseEntity<ResponseTokenDTO>(accountService.refreshToken(refToken), HttpStatus.OK);
	    }
	    catch(Exception e) {
	    	return new ResponseEntity<String>("Invalid Refresh Token!", HttpStatus.BAD_REQUEST);
	    }
	}
	
	@PostMapping("/register") //exposed
	public ResponseEntity<?> register(@Valid @RequestBody RegisterDTO registerDTO){
	    try {
	    	accountService.registerUser(registerDTO);
	    	return new ResponseEntity<String>("Successful", HttpStatus.OK);
	    }
	    catch(Exception e) {
	    	return new ResponseEntity<String>("Invalid details, try agian later!", HttpStatus.BAD_REQUEST);
	    }
	}
	
	@PostMapping("/forgot/{phoneNum:\\d{10}}") //exposed
	public ResponseEntity<?> forgot(@PathVariable String phoneNum){
		String phoneNumber = SanitizationUtils.sanitizeHtml(phoneNum);
		if(phoneNumber == null) {
			return new ResponseEntity<String>("Invalid request", HttpStatus.BAD_REQUEST);
		}
	    try {
	    	return new ResponseEntity<SecurityQuestionDTO>(accountService.getSecurityQuestionAndToken(phoneNumber), HttpStatus.OK);
	    }
	    catch(Exception e) {
	    	return new ResponseEntity<String>("Invalid details, try agian later!", HttpStatus.BAD_REQUEST);
	    }
	}
	
	@PostMapping("/forgot/answer")
	public ResponseEntity<?> securityAnswer(@Valid @RequestBody SecurityAnswerDTO securityAnswerDTO, HttpServletRequest request){
	    try {
	    	String authorizationHeader = request.getHeader("Authorization");
	        String token = authorizationHeader.substring(7);
	    	return new ResponseEntity<ResetTokenDTO>(accountService.answerSecurityQuestion(token, securityAnswerDTO), HttpStatus.OK);
	    }
	    catch(Exception e) {
	    	return new ResponseEntity<String>("Invalid details, try agian later!", HttpStatus.BAD_REQUEST);
	    }
	}
	
	@PutMapping("/forgot/reset")
	public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordDTO resetPasswordDTO, HttpServletRequest request){
	    try {
	    	String authorizationHeader = request.getHeader("Authorization");
	        String token = authorizationHeader.substring(7);
	        accountService.resetPassword(token, resetPasswordDTO);
	    	return new ResponseEntity<String>("Successful", HttpStatus.OK);
	    }
	    catch(Exception e) {
	    	return new ResponseEntity<String>("Failed, try agian later!", HttpStatus.BAD_REQUEST);
	    }
	}
	
	@DeleteMapping()
	public ResponseEntity<?> deleteAccount(@Valid @RequestBody RefreshTokenDTO refreshToken, HttpServletRequest request){
	    try {
	    	String authorizationHeader = request.getHeader("Authorization");
	        String token = authorizationHeader.substring(7);
	        accountService.deleteAccount(token);
	    	return new ResponseEntity<String>("Successful", HttpStatus.OK);
	    }
	    catch(Exception e) {
	    	return new ResponseEntity<String>("Failed, try agian later!", HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
}
