package com.example.demo.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.ContactDTO;
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
import com.example.demo.model.Message;
import com.example.demo.model.RefreshToken;
import com.example.demo.model.Security;
import com.example.demo.model.UserConversation;
import com.example.demo.model.UserMessageStatus;
import com.example.demo.model.UserMessageStatusId;
import com.example.demo.model.UserStatus;
import com.example.demo.repository.ContactRepository;
import com.example.demo.repository.ConversationRepository;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.RefreshTokenRepository;
import com.example.demo.repository.SecurityRepository;
import com.example.demo.repository.UserConversationRepository;
import com.example.demo.repository.UserMessageStatusRepository;
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
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	NotificationUtil notificationUtil;
	
	@Autowired
	MessageRepository messageRepository;
	
	@Autowired
	UserMessageStatusRepository userMessageStatusRepository;
	
	
	@Autowired
	ConversationRepository conversationRepository;
	
	public ResponseTokenDTO createTokenResponseDTO(String phoneNumber) {
		Optional<Contact> contactOptional = contactRepository.findByPhoneNumber(phoneNumber);
		Contact contact = contactOptional.get();
		
		ResponseTokenDTO reponseTokenDTO = new ResponseTokenDTO();
		reponseTokenDTO.setUserId(contact.getContactId());
		reponseTokenDTO.setUsername(contact.getUserName());
		reponseTokenDTO.setBase64Image(contact.getProfilePhoto() != null && contact.getProfilePhoto().length > 0 ? Base64.getEncoder().encodeToString(contact.getProfilePhoto()): null);
		reponseTokenDTO.setAccessToken(jwtUtil.GenerateToken(Long.toString(contact.getContactId())));
		reponseTokenDTO.setToken(refreshTokenService.createRefreshToken(phoneNumber).getToken());
		Notification notification = new Notification();
        notification.setType(NotificationType.ONLINE);
        notification.setSenderId(contact.getContactId());
        
        List<UserConversation> userConversations = userConversationRepository.findAllByContactId(contact.getContactId());
		
		
        for (UserConversation userConversation : userConversations) {
            Conversation conversation = userConversation.getConversation();
            notification.setConversationId(conversation.getConversationId());
            notification.setGroupName(conversation.getConversationName());
            if(conversation.getConversationName() == null) {
            	Contact friend = getReceiverId(contact, conversation);
                ContactDTO contactDTO = new ContactDTO(friend.getContactId(), friend.getUserName(), null, friend.getStatus(), null);
            	notification.setContact(contactDTO);
            }
            
            notificationUtil.sendNotification(notification);
            
        }
        
        markDeliveredForInvolvedConversations(contact);
        
		return reponseTokenDTO;
	}

	

	private void markDeliveredForInvolvedConversations(Contact contact) {
		List<UserConversation> userConversations = userConversationRepository.findAllByContactId(contact.getContactId());
		
		for(UserConversation userConversation: userConversations) {
			if(userConversation.getConversation().getConversationName() == null) {
				markMessagesAsDeliveredForPersonalConversation(contact, userConversation.getConversation());
			} else {
				markMessagesAsDeliveredForGroupConversation(contact, userConversation.getConversation());
			}
		}
		
	}

	private void markMessagesAsDeliveredForGroupConversation(Contact contact, Conversation conversation) {
		List<Message> undeliveredMessages = messageRepository.findByContactAndConversationAndIsDeliveredFalse(contact, conversation);
		
		for(Message message: undeliveredMessages) {
			UserMessageStatus userMessageStatus = userMessageStatusRepository.findById(new UserMessageStatusId(contact.getContactId(), message.getMessageId())).get();
			userMessageStatus.setDelivered(true);
			userMessageStatus.setDeliveredDateTime(LocalDateTime.now());
			userMessageStatusRepository.save(userMessageStatus);
			if(allMessageStatusDelivered(message)) {
				message.setDelivered(true);
				message.setDeliveredDateTime(LocalDateTime.now());
				messageRepository.save(message);
			}	
		}
		
	}



	private void markMessagesAsDeliveredForPersonalConversation(Contact contact, Conversation conversation) {
	    List<Message> undeliveredMessages = messageRepository.findByContactAndConversationAndIsDeliveredFalse(contact, conversation);
	    
	    for (Message message : undeliveredMessages) {
	        message.setDelivered(true);
	        message.setDeliveredDateTime(LocalDateTime.now());
	        messageRepository.save(message);
	    }
	}


	public void logOutUser(String token) throws Exception {
		String userId = jwtUtil.extractUsername(token);
		Contact contact = contactRepository.findById(Long.parseLong(userId)).get();
		
		
		Optional<RefreshToken> refreshToken = refreshTokenRepository.findByContact(contact);	
		if(refreshToken.isPresent()) {
			try {
			refreshTokenRepository.deleteByContact(contact);
			}
			catch(Exception e) {
				System.out.println("adasd");
			}
		} else {
			throw new Exception("Error when logging out");
		}
		
		Notification notification = new Notification();
        notification.setType(NotificationType.OFFLINE);
        notification.setSenderId(contact.getContactId());
        
        List<UserConversation> userConversations = userConversationRepository.findAllByContactId(contact.getContactId());
		
		
        for (UserConversation userConversation : userConversations) {
            Conversation conversation = userConversation.getConversation();
            notification.setConversationId(conversation.getConversationId());
            notification.setGroupName(conversation.getConversationName());
            if(conversation.getConversationName() == null) {
            	Contact friend = getReceiverId(contact, conversation);
                ContactDTO contactDTO = new ContactDTO(friend.getContactId(), friend.getUserName(), null, friend.getStatus(), null);
            	notification.setContact(contactDTO);
            }
            
            notificationUtil.sendNotification(notification);
            
        }	
	}
	
	public ResponseTokenDTO refreshToken(String refershToken) {
		Optional<RefreshToken> refreshToken = refreshTokenService.findByToken(refershToken);
		if(refreshToken.isPresent()) {
			Contact contact = refreshToken.get().getContact();
			if(contact.getSecurity().isLocked()) {
				throw new AccountLockedException();
			}
		} else {
			throw new InvalidRefreshTokenException();
		}
		return refreshToken
        	.map(refreshTokenService::verifyExpiration)
        	.map(RefreshToken::getContact)
        	.map(contact -> {
        		String accessToken = jwtUtil.GenerateToken(Long.toString(contact.getContactId()));
        		return new ResponseTokenDTO(contact.getContactId(), contact.getUserName(), contact.getProfilePhoto() != null && contact.getProfilePhoto().length > 0 ? Base64.getEncoder().encodeToString(contact.getProfilePhoto()): null, accessToken, refershToken); 
        	}).orElseThrow(() ->new RuntimeException("Refresh Token expired!!"));
	}
	
	public void registerUser(RegisterDTO registerDTO) throws Exception {
		Optional<Contact> existing = contactRepository.findByPhoneNumber(registerDTO.getPhoneNumber());
		if(existing.isPresent()) {
			throw new Exception("User with" + registerDTO.getPhoneNumber() + "already exists!");
		}
		if( registerDTO.getPassword().compareTo(registerDTO.getConfirmPassword()) != 0) {
			throw new Exception("Password Mismatch!");
		}
		Contact contact = new Contact();
		contact.setUserName(registerDTO.getUserName());
		contact.setPhoneNumber(registerDTO.getPhoneNumber());
		contact.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
		contact.setStatus(UserStatus.OFFLINE);
		Security security = new Security();
		security.setQuestion(registerDTO.getSecurityQuestion());
		security.setAnswer(registerDTO.getSecurityAnswer());
		security.setAccountStatus(AccountStatus.OK);
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
	    securityQuestionDTO.setToken(jwtUtil.GenerateToken(Long.toString(contact.getContactId()), 5, "answer"));
	    return securityQuestionDTO;
	}

	
	public ResetTokenDTO answerSecurityQuestion(String token, SecurityAnswerDTO securityAnswerDTO) {
		String contactId = jwtUtil.extractUsername(token);
		Optional<Contact> contactOptional = contactRepository.findById(Long.parseLong(contactId));
		if(contactOptional.get().getSecurity().getAnswer().compareTo(securityAnswerDTO.getAnswer()) != 0) {
			throw new InvalidSecurityAnswerException();
		}
		ResetTokenDTO resetTokenDTO = new ResetTokenDTO();
		resetTokenDTO.setToken(jwtUtil.GenerateToken(Long.toString(contactOptional.get().getContactId()), 5, "reset"));
		return resetTokenDTO;
	}
	
	public void resetPassword(String token, ResetPasswordDTO resetPasswordDTO) {
		String contactId = jwtUtil.extractUsername(token);
		Optional<Contact> contactOptional = contactRepository.findById(Long.parseLong(contactId));
		Contact contact = contactOptional.get();
		contact.setPassword(passwordEncoder.encode(resetPasswordDTO.getPassword()));
		contactRepository.save(contact);
	}
	
	
	public void deleteAccount(String token){
		String contactId = jwtUtil.extractUsername(token);
		Optional<Contact> contactOptional = contactRepository.findById(Long.parseLong(contactId));
		Optional<RefreshToken> refreshTokenOptional = refreshTokenService.findByToken(token);
		refreshTokenOptional.orElseThrow(()-> new InvalidRefreshTokenException());
		RefreshToken refreshToken = refreshTokenOptional.get();
		refreshTokenService.verifyExpiration(refreshToken);
		
		refreshTokenService.deleteRefreshToken(refreshToken);
		
		deleteConversationsMessagesAndAccount(contactOptional.get());
	}
	
	
	
	private void deleteConversationsMessagesAndAccount(Contact contact) {
		
		List<UserConversation> userConversations = userConversationRepository.findAllByContactId(contact.getContactId());
		
		for(UserConversation userConversation: userConversations) {
			Conversation conversation = userConversation.getConversation();
			
			List<Message> messages = messageRepository.findByContactAndConversation(contact, conversation);
			
			if(conversation.getConversationName() != null) {
				for(Message message: messages) {
					deletegroupMessagingStatus(message);
				}
			}
			messageRepository.deleteAll(messages);
			
			userConversationRepository.delete(userConversation);
			
			if(conversation.getConversationName() == null) {
				conversationRepository.delete(conversation);
			}
			
		}
	
		contactRepository.delete(contact);
	}

	private void deletegroupMessagingStatus(Message message) {
		List<UserMessageStatus> userMessageStatusList = userMessageStatusRepository.findAllByMessage(message);
		userMessageStatusRepository.deleteAll(userMessageStatusList);
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
	
	public Contact getReceiverId(Contact contact, Conversation conversation) {
		try {
		  List<Contact> contacts = userConversationRepository.findAllByConversation(conversation);
		  for (Contact con : contacts) {
			    if (con.getContactId() != contact.getContactId()) {
			      return con;
			    }
			  }
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
		  
		  return null;
	}
	
	private boolean allMessageStatusDelivered(Message message) {
	    List<UserMessageStatus> userMessageStatusList = userMessageStatusRepository.findAllByMessage(message);
	    for (UserMessageStatus ums : userMessageStatusList) {
	        if(!ums.isDelivered()) {
	        	return false;
	        }
	    }
	    return true;
	}
	
}
