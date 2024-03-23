package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.dto.ChatDTO;
import com.example.demo.dto.ContactDTO;
import com.example.demo.dto.GroupCreateDTO;
import com.example.demo.dto.MessageDTO;
import com.example.demo.dto.MessageFront;
import com.example.demo.dto.MessageType;
import com.example.demo.dto.Notification;
import com.example.demo.dto.NotificationType;
import com.example.demo.dto.UserStatusDTO;
import com.example.demo.exceptions.InvalidRequestException;
import com.example.demo.model.Contact;
import com.example.demo.model.Conversation;
import com.example.demo.model.ConversationStatus;
import com.example.demo.model.Message;
import com.example.demo.model.UserConversation;
import com.example.demo.model.UserConversationId;
import com.example.demo.model.UserMessageStatus;
import com.example.demo.model.UserMessageStatusId;
import com.example.demo.model.UserRole;
import com.example.demo.model.UserStatus;
import com.example.demo.repository.ContactRepository;
import com.example.demo.repository.ConversationRepository;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.UserConversationRepository;
import com.example.demo.repository.UserMessageStatusRepository;
import com.example.demo.utils.DateFormat;
import com.example.demo.utils.JWTUtil;
import com.example.demo.utils.NotificationUtil;

@Service
public class MessageService {
	
	
	@Autowired
	ContactRepository contactRepository;
	
	@Autowired
	ConversationRepository conversationRepository;
	
	@Autowired
	UserConversationRepository userConversationRepository;
	
	@Autowired
	MessageRepository messageRepository;
	
	@Autowired
	UserMessageStatusRepository userMessageStatusRepository;
	
	@Autowired
	JWTUtil jwtUtil;
	
	@Autowired
	DateFormat dateFormat;
	
	@Autowired
	NotificationUtil notificationUtil;
	
	
	public List<MessageDTO> getMessages(String token, Long conversationId, int offset) throws InvalidRequestException {
		String contactId = jwtUtil.extractUsername(token);
		Optional<Contact> contactOptional = contactRepository.findById(Long.parseLong(contactId));
		contactOptional.orElseThrow(()-> new UsernameNotFoundException("Not Found: " + contactId));
		Contact contact = contactOptional.get();
		
		Optional<UserConversation> userConversationOptional = userConversationRepository.findById(new UserConversationId(contact.getContactId(), conversationId));
		userConversationOptional.orElseThrow(()-> new InvalidRequestException("Not Found: " + contactId));
		UserConversation userConversation = userConversationOptional.get();
		
		if(userConversation.getStatus() != ConversationStatus.BLOCKED) {
			Pageable pageable = PageRequest.of(offset, 100);
			Page<Message> recentMessagesPage = messageRepository.findMessagesByConversation(userConversation.getConversation(), pageable);
			List<Message> recentMessages = recentMessagesPage.getContent();
			List<MessageDTO> messageDTOs = new ArrayList<>();
			for(Message message: recentMessages) {
				if(message.getSentDatetime().isAfter(userConversation.getJoinedDatetime())) {
					MessageDTO messageDTO = new MessageDTO();
					messageDTO.setSenderId(message.getContact().getContactId());
					messageDTO.setMessageId(message.getMessageId());
					messageDTO.setMessage(message.getMessageText());
					if(userConversation.getConversation().getConversationName() != null) {
						List<UserMessageStatus> userMessageStatus = userMessageStatusRepository.findAllByMessage(message);
						List<UserStatusDTO> userStatusDTOs = new ArrayList<>();
						for(UserMessageStatus ums: userMessageStatus) {
							UserStatusDTO userStatusDTO = new UserStatusDTO();
							userStatusDTO.setUserId(ums.getContact().getContactId());
							userStatusDTO.setRead(ums.isRead());
							userStatusDTO.setReadDateTime(ums.getReadDateTime() != null ? dateFormat.formatLocalDateTime(ums.getReadDateTime()): null);
							userStatusDTO.setDelivered(ums.isDelivered());
							userStatusDTO.setDeliveredDateTime(ums.getDeliveredDateTime() != null ?dateFormat.formatLocalDateTime(ums.getDeliveredDateTime()): null);
							userStatusDTOs.add(userStatusDTO);
						}
						messageDTO.setIsReadInfo(userStatusDTOs);
					}
					messageDTO.setRead(message.isRead());
					messageDTO.setReadDateTime(message.getReadDateTime() != null ? dateFormat.formatLocalDateTime(message.getReadDateTime()) : null);
					messageDTO.setSentDateTime(dateFormat.formatLocalDateTime(message.getSentDatetime()));
					messageDTO.setDelivered(message.isDelivered());
					messageDTO.setDeliveredDateTime(message.getDeliveredDateTime() != null ? dateFormat.formatLocalDateTime(message.getDeliveredDateTime()): null);
					messageDTOs.add(messageDTO);
				} else {
					break;
				}
			}
			return messageDTOs;
		}
		
		return null;
	}

	public Long getReceiverId(Long senderId, Long conversationId) {
		Optional<Conversation> conversationOptional = conversationRepository.findById(conversationId);
		if(conversationOptional.isEmpty()) {
			return null;
		}
		List<Contact> contacts =  userConversationRepository.findAllByConversation(conversationOptional.get());
	
		for(Contact contact: contacts) {
			if(contact.getContactId() != senderId) {
				return contact.getContactId();
			}
		}
		return null;
	}


	public List<ChatDTO> getConversations(String token) throws InvalidRequestException {
		String contactId = jwtUtil.extractUsername(token);
		Optional<Contact> contactOptional = contactRepository.findById(Long.parseLong(contactId));
		Contact contact = contactOptional.get();
		
		List<UserConversation> userConversations = userConversationRepository.findAllByContactId(contact.getContactId());
		
		
		List<ChatDTO> chats = new ArrayList<>();
		
		if(userConversations.isEmpty()) {
			return new ArrayList<>();
		}
		
		for(UserConversation userConversation: userConversations) {
			ChatDTO chat = new ChatDTO();
			chat.setConversationId(userConversation.getConversation().getConversationId());
			chat.setStatus(userConversation.getStatus());
			chat.setUserRole(userConversation.getRole());
			if (userConversation.getConversation().getConversationName() != null) {
		        chat.setName(userConversation.getConversation().getConversationName());
		        String profile = userConversation.getConversation().getProfilePhoto() != null && userConversation.getConversation().getProfilePhoto().length > 0 ? Base64.getEncoder().encodeToString(userConversation.getConversation().getProfilePhoto()) : null;
		        chat.setGroupProfile(profile);
		    } else {
		        chat.setName(null);
		    }
			List<Contact> contacts = userConversationRepository.findAllByConversation(userConversation.getConversation());
			List<ContactDTO> contactDTOs = new ArrayList<>();
			for(Contact con: contacts) {
				if(con.getContactId().compareTo(contact.getContactId()) != 0) {
					ContactDTO contactDTO = new ContactDTO();
					contactDTO.setcontactId(con.getContactId());
					contactDTO.setUsername(con.getUserName());
					contactDTO.setBase64Image( con.getProfilePhoto() != null && con.getProfilePhoto().length > 0? Base64.getEncoder().encodeToString(con.getProfilePhoto()): null);
					contactDTO.setStatus(con.getStatus());
					UserConversation userConversationOther = userConversationRepository.findById(new UserConversationId(con.getContactId(), userConversation.getConversation().getConversationId())).get();
					contactDTO.setRole(userConversationOther.getRole());
					contactDTOs.add(contactDTO);
				}
			}
			chat.setContacts(contactDTOs);
			List<MessageDTO> messages = getMessages(token, userConversation.getConversation().getConversationId(), 0);
			chat.setMessages(messages);
			chats.add(chat);
		}
		
		return chats;
	}
	
	
	public ChatDTO getConversation(String token, Long conversationId) throws InvalidRequestException {
		String contactId = jwtUtil.extractUsername(token);
		Optional<Contact> contactOptional = contactRepository.findById(Long.parseLong(contactId));
		Contact contact = contactOptional.get();
		
		Optional<Conversation> conversationOptional = conversationRepository.findById(conversationId);
		conversationOptional.orElseThrow(() -> new InvalidRequestException("Invalid request!"));
		
		Optional<UserConversation> userConversationOptional = userConversationRepository.findById(new UserConversationId(contact.getContactId(), conversationId));
		userConversationOptional.orElseThrow(() -> new InvalidRequestException("Invalid request!"));
		UserConversation userConversation = userConversationOptional.get();
		
		ChatDTO chat = new ChatDTO();
		chat.setConversationId(userConversation.getConversation().getConversationId());
		chat.setStatus(userConversation.getStatus());
		chat.setUserRole(userConversation.getRole());
		if (userConversation.getConversation().getConversationName() != null) {
	        chat.setName(userConversation.getConversation().getConversationName());
	        String profile = userConversation.getConversation().getProfilePhoto() != null && userConversation.getConversation().getProfilePhoto().length > 0 ? Base64.getEncoder().encodeToString(userConversation.getConversation().getProfilePhoto()) : null;
	        chat.setGroupProfile(profile);
	    } else {
	        chat.setName(null);
	    }
		List<Contact> contacts = userConversationRepository.findAllByConversation(userConversation.getConversation());
		List<ContactDTO> contactDTOs = new ArrayList<>();
		for(Contact con: contacts) {
			if(con.getContactId().compareTo(contact.getContactId()) != 0) {
				ContactDTO contactDTO = new ContactDTO();
				contactDTO.setcontactId(con.getContactId());
				contactDTO.setUsername(con.getUserName());
				contactDTO.setBase64Image( con.getProfilePhoto() != null && con.getProfilePhoto().length > 0? Base64.getEncoder().encodeToString(con.getProfilePhoto()): null);
				contactDTO.setStatus(con.getStatus());
				UserConversation userConversationOther = userConversationRepository.findById(new UserConversationId(con.getContactId(), userConversation.getConversation().getConversationId())).get();
				contactDTO.setRole(userConversationOther.getRole());
				contactDTOs.add(contactDTO);
			}
		}
		chat.setContacts(contactDTOs);
		
		return chat;
	}


	public MessageDTO sendMessage(String token, MessageFront messageFront) throws InvalidRequestException {
		String contactId = jwtUtil.extractUsername(token);
		Optional<Contact> contactOptional = contactRepository.findById(Long.parseLong(contactId));
		
		Optional<Conversation> conversationOptional = conversationRepository.findById(Long.parseLong(messageFront.getConversationId()));
		conversationOptional.orElseThrow(() -> new InvalidRequestException("Invalid request!"));
		
		Optional<UserConversation> userConversationOptional = userConversationRepository.findById(new UserConversationId(contactOptional.get().getContactId(), Long.parseLong(messageFront.getConversationId())));
		userConversationOptional.orElseThrow(() -> new InvalidRequestException("Invalid request!"));
		UserConversation userConversation = userConversationOptional.get();
		
		if(userConversationOptional.get().getStatus() != ConversationStatus.BLOCKED) {
			Message message = new Message();
		    message.setContact(contactOptional.get());
		    message.setConversation(conversationOptional.get());
		    message.setMessageText(messageFront.getMessage());
		    message.setRead(false);
		    message.setSentDatetime(LocalDateTime.now());
		    message.setReadDateTime(null);
		    
		    Contact other = null;
		    if(conversationOptional.get().getConversationName() == null) {
		    	List<Contact> contacts = userConversationRepository.findAllByConversation(conversationOptional.get());
		    	for(Contact contact: contacts) {
		    		if(contact.getContactId() != contactOptional.get().getContactId()) {
		    			other = contact;
		    			break;
		    		}
		    	}
		    }
		    
		    if(userConversation.getConversation().getConversationName() == null) {
		    	message.setDelivered(other.getStatus() == UserStatus.ONLINE);
		    	if(message.isDelivered()) {
		    		message.setDeliveredDateTime(LocalDateTime.now());
		    	}
		    } else {
		    	groupMessagingStatus(message);
		    	if(allMessageStatusDelivered(message)) {
		    		message.setDelivered(true);
		    		message.setDeliveredDateTime(LocalDateTime.now());
		    	}
		    }
		    
		    message = messageRepository.save(message);
		    
		    
			
		    Notification notification = new Notification();
		    notification.setType(NotificationType.SENT);
		    notification.setGroupName(conversationOptional.get().getConversationName() );
		    notification.setConversationId(conversationOptional.get().getConversationId());
		    notification.setSenderId(contactOptional.get().getContactId());
		    
		    
		    
		    sendNotification(notification, message, other);
		    
		    MessageDTO messageDTO = new MessageDTO();
			messageDTO.setSenderId(message.getContact().getContactId());
			messageDTO.setMessageId(message.getMessageId());
			messageDTO.setMessage(message.getMessageText());
			if(userConversation.getConversation().getConversationName() != null) {
				List<UserMessageStatus> userMessageStatus = userMessageStatusRepository.findAllByMessage(message);
				List<UserStatusDTO> userStatusDTOs = new ArrayList<>();
				for(UserMessageStatus ums: userMessageStatus) {
					UserStatusDTO userStatusDTO = new UserStatusDTO();
					userStatusDTO.setUserId(ums.getContact().getContactId());
					userStatusDTO.setRead(ums.isRead());
					userStatusDTO.setReadDateTime(ums.getReadDateTime() != null? dateFormat.formatLocalDateTime(ums.getReadDateTime()): null);
					userStatusDTO.setDelivered(ums.isDelivered());
					userStatusDTO.setDeliveredDateTime(ums.getDeliveredDateTime() != null ? dateFormat.formatLocalDateTime(ums.getDeliveredDateTime()): null);
					userStatusDTOs.add(userStatusDTO);
				}
				messageDTO.setIsReadInfo(userStatusDTOs);
			}
			messageDTO.setRead(message.isRead());
			messageDTO.setReadDateTime(message.getReadDateTime() != null ? dateFormat.formatLocalDateTime(message.getReadDateTime()): null);
			messageDTO.setSentDateTime(dateFormat.formatLocalDateTime(message.getSentDatetime()));
			messageDTO.setDelivered(message.isDelivered());
			messageDTO.setDeliveredDateTime(message.getDeliveredDateTime() != null ? dateFormat.formatLocalDateTime(message.getDeliveredDateTime()): null);
			return messageDTO;
		} else {
			throw new InvalidRequestException("Invalid Request");
		}
		
		
	}
	
	public void deleteMessage(String token, MessageFront messageFront) throws InvalidRequestException {
		String contactId = jwtUtil.extractUsername(token);
		Optional<Contact> contactOptional = contactRepository.findById(Long.parseLong(contactId));
		
		Optional<Conversation> conversationOptional = conversationRepository.findById(Long.parseLong(messageFront.getConversationId()));
		conversationOptional.orElseThrow(() -> new InvalidRequestException("Invalid request!"));
		
		Optional<UserConversation> userConversationOptional = userConversationRepository.findById(new UserConversationId(contactOptional.get().getContactId(), Long.parseLong( messageFront.getConversationId())));
		userConversationOptional.orElseThrow(() -> new InvalidRequestException("Invalid request!"));
		
		if(userConversationOptional.get().getStatus() != ConversationStatus.BLOCKED) {
			Notification notification = new Notification();
		    notification.setType(NotificationType.DELETED);
		    notification.setGroupName(conversationOptional.get().getConversationName() );
		    notification.setConversationId(conversationOptional.get().getConversationId());
		    notification.setSenderId(contactOptional.get().getContactId());
		    
			Optional<Message> messageOptional = messageRepository.findById(Long.parseLong( messageFront.getMessageId()));
			messageOptional.orElseThrow(() -> new InvalidRequestException("Invalid Request!"));
	        
            Message message = messageOptional.get();
            if(conversationOptional.get().getConversationName() != null) {
	        	deletegroupMessagingStatus(message);
	        }
            messageRepository.delete(message);

		    Contact other = null;
		    if(conversationOptional.get().getConversationName() == null) {
		    	List<Contact> contacts = userConversationRepository.findAllByConversation(conversationOptional.get());
		    	
		    	for(Contact contact: contacts) {
		    		if(contact.getContactId() != contactOptional.get().getContactId()) {
		    			other = contact;
		    			break;
		    		}
		    	}	
		    }
		    
		    sendNotification(notification, message, other);
			
		} else {
			throw new InvalidRequestException("Invalid Request");
		}
		
	}
	
	public void readMessage(String token, Long conversationId) throws InvalidRequestException {
		String contactId = jwtUtil.extractUsername(token);
		Optional<Contact> contactOptional = contactRepository.findById(Long.parseLong(contactId));
		
		Optional<Conversation> conversationOptional = conversationRepository.findById(conversationId);
		conversationOptional.orElseThrow(() -> new InvalidRequestException("Invalid request!"));
		Conversation conversation = conversationOptional.get();
		
		Optional<UserConversation> userConversationOptional = userConversationRepository.findById(new UserConversationId(contactOptional.get().getContactId(), conversationId));
		userConversationOptional.orElseThrow(() -> new InvalidRequestException("Invalid request!"));
		
		if(userConversationOptional.get().getStatus() != ConversationStatus.BLOCKED) {
			Notification notification = new Notification();
		    notification.setType(NotificationType.READ);
		    notification.setGroupName(conversationOptional.get().getConversationName() );
		    notification.setConversationId(conversationOptional.get().getConversationId());
		    notification.setSenderId(contactOptional.get().getContactId());
		    
			List<Message> messages = messageRepository.findByConversationAndIsReadFalse(conversation);
	        
			for(Message message: messages) {
	            if(conversationOptional.get().getConversationName() != null) {
		        	updateMessageStatus(message, contactOptional.get());
		        	if(allMessageStatusRead(message)) {
		        		message.setRead(true);
			        	message.setReadDateTime(LocalDateTime.now());
			        	messageRepository.save(message);
		        	}
		        }else {
		        	message.setRead(true);
		        	message.setReadDateTime(LocalDateTime.now());
		        	messageRepository.save(message);
		        }
			}
            
		    sendNotification(notification, null, contactOptional.get());
			
		} else {
			throw new InvalidRequestException("Invalid Request");
		}
		
	}
	
	
	
	public void sendNotification(Notification notification, Message message, Contact other) {
		
		if(message != null) {
			MessageDTO messageDTO = new MessageDTO();
			
			messageDTO.setRead(message.isRead());
	        messageDTO.setReadDateTime(message.getReadDateTime()!= null ? dateFormat.formatLocalDateTime(message.getReadDateTime()): null);
			messageDTO.setSenderId(message.getContact().getContactId());
		    messageDTO.setMessage(message.getMessageText());
		    messageDTO.setMessageId(message.getMessageId());
		    messageDTO.setSentDateTime(dateFormat.formatLocalDateTime(message.getSentDatetime()));
		    messageDTO.setDelivered(message.isDelivered());
		    messageDTO.setDeliveredDateTime(message.getReadDateTime()!= null ? dateFormat.formatLocalDateTime(message.getDeliveredDateTime()): null);
		    
		    notification.setMessageDTO(messageDTO);
		}
	    if(notification.getGroupName() == null) {
	    	ContactDTO contactDTO = new ContactDTO();
			contactDTO.setcontactId(other.getContactId());
			contactDTO.setUsername(other.getUserName());
			contactDTO.setBase64Image( other.getProfilePhoto() != null && other.getProfilePhoto().length > 0 ? Base64.getEncoder().encodeToString(other.getProfilePhoto()) : null);
			contactDTO.setStatus(other.getStatus());
			notification.setContact(contactDTO);
			
	    }
	   notificationUtil.sendNotification(notification);
	    
		
	}
	
	private void deletegroupMessagingStatus(Message message) {
		List<UserMessageStatus> userMessageStatusList = userMessageStatusRepository.findAllByMessage(message);
		userMessageStatusRepository.deleteAll(userMessageStatusList);
	}


	private void groupMessagingStatus(Message message) {
		List<Contact> participants = userConversationRepository.findAllByConversation(message.getConversation());
		List<UserMessageStatus> userMessageStatuses = new ArrayList<>();
		for(Contact contact: participants) {
			if(contact.getContactId() != message.getContact().getContactId()) {
				UserMessageStatus userMessageStatus = new UserMessageStatus();
				userMessageStatus.setId(new UserMessageStatusId(message.getContact().getContactId(), message.getMessageId()));
				userMessageStatus.setContact(contact);
				userMessageStatus.setMessage(message);
				userMessageStatus.setRead(false);
				userMessageStatus.setReadDateTime(null);
				userMessageStatus.setDelivered(contact.getStatus() == UserStatus.ONLINE);
				if(userMessageStatus.isDelivered()) {
					userMessageStatus.setDeliveredDateTime(LocalDateTime.now());
				}
				userMessageStatuses.add(userMessageStatus);
			}
		}
		userMessageStatusRepository.saveAll(userMessageStatuses);
	}
	
	private void updateMessageStatus(Message message, Contact reader) {
	    List<UserMessageStatus> userMessageStatusList = userMessageStatusRepository.findAllByMessage(message);
	    for (UserMessageStatus ums : userMessageStatusList) {
	        if (ums.getContact().equals(reader)) {
	            ums.setRead(true);
	            ums.setReadDateTime(LocalDateTime.now());
	            userMessageStatusRepository.save(ums);
	            break;
	        }
	    }
	}
	private boolean allMessageStatusRead(Message message) {
	    List<UserMessageStatus> userMessageStatusList = userMessageStatusRepository.findAllByMessage(message);
	    for (UserMessageStatus ums : userMessageStatusList) {
	        if(!ums.isRead()) {
	        	return false;
	        }
	    }
	    return true;
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
