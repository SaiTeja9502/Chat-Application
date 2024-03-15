package com.example.demo.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.dto.MessageFront;
import com.example.demo.dto.Notification;
import com.example.demo.dto.NotificationType;
import com.example.demo.dto.RegisterDTO;
import com.example.demo.dto.ResetPasswordDTO;
import com.example.demo.dto.ResetTokenDTO;
import com.example.demo.dto.ResponseTokenDTO;
import com.example.demo.dto.SecurityAnswerDTO;
import com.example.demo.dto.SecurityQuestionDTO;
import com.example.demo.exceptions.AccountLockedException;
import com.example.demo.exceptions.InvalidRefreshTokenException;
import com.example.demo.exceptions.InvalidSecurityAnswerException;
import com.example.demo.model.AccountStatus;
import com.example.demo.model.Contact;
import com.example.demo.model.Conversation;
import com.example.demo.model.RefreshToken;
import com.example.demo.model.Security;
import com.example.demo.model.UserConversation;
import com.example.demo.repository.ContactRepository;
import com.example.demo.repository.RefreshTokenRepository;
import com.example.demo.repository.SecurityRepository;
import com.example.demo.repository.UserConversationRepository;
import com.example.demo.utils.JWTUtil;
import com.example.demo.utils.NotificationUtil;

@Service
public class AccountService {
	
	@Autowired
	ContactRepository contactRepository;
	
	@Autowired
	RefreshTokenRepository refreshTokenRepository;
	
	@Autowired
	SecurityRepository securityRepository;
	
	@Autowired
	UserConversationRepository userConversationRepository;
	
	@Autowired
	JWTUtil jwtUtil;
	
	@Autowired
	RefreshTokenService refreshTokenService;
	
	@Autowired
	NotificationUtil notificationUtil;
	
	@Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
	
	public ResponseTokenDTO createTokenResponseDTO(String phoneNumber) {
		Optional<Contact> contactOptional = contactRepository.findByPhoneNumber(phoneNumber);
		contactOptional.orElseThrow(()-> new UsernameNotFoundException("Not Found: " + phoneNumber));
		Contact contact = contactOptional.get();
		if(contact.getSecurity().isLocked()) {
			throw new AccountLockedException();
		}
		ResponseTokenDTO reponseTokenDTO = new ResponseTokenDTO();
		reponseTokenDTO.setAccessToken(jwtUtil.GenerateToken(contact.getContactId().toString()));
		reponseTokenDTO.setToken(refreshTokenService.createRefreshToken(phoneNumber).getToken());
		
		Notification notification = new Notification();
        notification.setType(NotificationType.ONLINE);
        notification.setSenderId(contact.getContactId());
		notificationUtil.sendNotifications(contact, notification);	
		return reponseTokenDTO;
	}

	

	public void logOutUser(String token) throws Exception {
		String userId = jwtUtil.extractUsername(token);
		Contact contact = contactRepository.findById(Long.parseLong(userId)).get();
		refreshTokenRepository.deleteByContact(contact);
		
		Optional<RefreshToken> refreshToken = refreshTokenRepository.findByContact(contact);
		Notification notification = new Notification();
        notification.setType(NotificationType.OFFLINE);
        notification.setSenderId(contact.getContactId());
		notificationUtil.sendNotifications(contact, notification);	
		if(refreshToken.isPresent()) {
			throw new Exception("Error when logging out");
		}
	}
	
	public ResponseTokenDTO refreshToken(String refershToken) {
		Optional<RefreshToken> refreshToken = refreshTokenService.findByToken(refershToken);
		if(refreshToken.isPresent()) {
			Contact contact = refreshToken.get().getContact();
			if(contact.getSecurity().isLocked()) {
				throw new AccountLockedException();
			}
		}
		return refreshToken
        	.map(refreshTokenService::verifyExpiration)
        	.map(RefreshToken::getContact)
        	.map(contact -> {
        		String accessToken = jwtUtil.GenerateToken(contact.getContactId().toString());
        		return new ResponseTokenDTO(accessToken, refershToken); 
        	}).orElseThrow(() ->new RuntimeException("Refresh Token is not in DB..!!"));
	}
	
	public void registerUser(RegisterDTO registerDTO) throws Exception {
		Optional<Contact> existing = contactRepository.findByPhoneNumber(registerDTO.getPhoneNumber());
		if(existing.isPresent()) {
			throw new Exception("User with" + registerDTO.getPhoneNumber() + "already exists!");
		}
		Contact contact = new Contact();
		contact.setUserName(registerDTO.getUserName());
		contact.setPhoneNumber(registerDTO.getPhoneNumber());
		contact.setPassword(registerDTO.getPassword());
		Security security = new Security();
		security.setQuestion(registerDTO.getSecurityQuestion());
		security.setAnswer(registerDTO.getSecurityAnswer());
		contact.setSecurity(security);
		securityRepository.save(security);
		contactRepository.save(contact);
		
	}
	
	public SecurityQuestionDTO getSecurityQuestionAndToken(String phoneNumber) {
	    Optional<Contact> contactOptional = contactRepository.findByPhoneNumber(phoneNumber);
	    Contact contact = contactOptional.orElseThrow(() -> new UsernameNotFoundException("Not Found: " + phoneNumber));
	    Security security = contact.getSecurity();

	    perfromSecurityCheck(security);

	    SecurityQuestionDTO securityQuestionDTO = new SecurityQuestionDTO();
	    securityQuestionDTO.setSecurityQuestion(contact.getSecurity().getQuestion());
	    securityQuestionDTO.setToken(jwtUtil.GenerateToken(contact.getContactId().toString(), 5, "answer"));
	    return securityQuestionDTO;
	}

	
	public ResetTokenDTO answerSecurityQuestion(String token, SecurityAnswerDTO securityAnswerDTO) {
		String contactId = jwtUtil.extractUsername(token);
		Optional<Contact> contactOptional = contactRepository.findById(Long.parseLong(contactId));
		contactOptional.orElseThrow(()-> new UsernameNotFoundException("Not Found: " + contactId));
		if(contactOptional.get().getSecurity().getAnswer().compareTo(securityAnswerDTO.getAnswer()) != 0) {
			throw new InvalidSecurityAnswerException();
		}
		ResetTokenDTO resetTokenDTO = new ResetTokenDTO();
		resetTokenDTO.setToken(jwtUtil.GenerateToken(contactOptional.get().getContactId().toString(), 5, "reset"));
		return resetTokenDTO;
	}
	
	public void resetPassword(String token, ResetPasswordDTO resetPasswordDTO) {
		String contactId = jwtUtil.extractUsername(token);
		Optional<Contact> contactOptional = contactRepository.findById(Long.parseLong(contactId));
		contactOptional.orElseThrow(()-> new UsernameNotFoundException("Not Found: " + contactId));
		Contact contact = contactOptional.get();
		contact.setPassword(resetPasswordDTO.getPassword());
		contactRepository.save(contact);
	}
	
	public void deleteAccount(String token){
		String contactId = jwtUtil.extractUsername(token);
		Optional<Contact> contactOptional = contactRepository.findById(Long.parseLong(contactId));
		contactOptional.orElseThrow(()-> new UsernameNotFoundException("Not Found: " + contactId));
		Optional<RefreshToken> refreshTokenOptional = refreshTokenService.findByToken(token);
		refreshTokenOptional.orElseThrow(()-> new InvalidRefreshTokenException());
		RefreshToken refreshToken = refreshTokenOptional.get();
		refreshTokenService.verifyExpiration(refreshToken);
		
		contactRepository.delete(contactOptional.get());
	}
	
	
	
	//private methods
	private void perfromSecurityCheck(Security security) {
		if (security.isLocked()) {
	        LocalDateTime lockedTime = security.getLockedLocalDateTime();
	        LocalDateTime currentTime = LocalDateTime.now();
	        long hoursPassed = ChronoUnit.HOURS.between(lockedTime, currentTime);
	        if (hoursPassed >= 24) {
	            security.setAccountStatus(AccountStatus.OK);
	            security.setTries(0);
	            security.setLockedLocalDateTime(null); 
	        } else {
	            throw new AccountLockedException();
	        }
	    }
	    int newTries = security.getTries() + 1;
	    security.setTries(newTries);
	    securityRepository.save(security);
	    if (newTries > 3) {
	        security.setAccountStatus(AccountStatus.LOCKED);
	        security.setLockedLocalDateTime(LocalDateTime.now());
	        throw new AccountLockedException();
	    }
	}
	
}
