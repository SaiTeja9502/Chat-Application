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
import com.example.demo.model.Contact;
import com.example.demo.model.Conversation;
import com.example.demo.model.ConversationStatus;
import com.example.demo.model.Message;
import com.example.demo.model.UserConversation;
import com.example.demo.model.UserConversationId;
import com.example.demo.model.UserMessageStatus;
import com.example.demo.model.UserMessageStatusId;
import com.example.demo.model.UserRole;
import com.example.demo.repository.ContactRepository;
import com.example.demo.repository.ConversationRepository;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.UserConversationRepository;
import com.example.demo.repository.UserMessageStatusRepository;
import com.example.demo.utils.JWTUtil;

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
	
	
	public List<ChatDTO> getAllMessages(String token, int offset) {
		String contactId = jwtUtil.extractUsername(token);
		Optional<Contact> contactOptional = contactRepository.findById(Long.parseLong(contactId));
		contactOptional.orElseThrow(()-> new UsernameNotFoundException("Not Found: " + contactId));
		Contact contact = contactOptional.get();
		
		List<UserConversation> userConversations = userConversationRepository.findAllByContact(contact);
		
		List<ChatDTO> chats = new ArrayList<>();
		
		for(UserConversation userConversation: userConversations) {
			ChatDTO chat = new ChatDTO();
			chat.setConversationId(userConversation.getConversation().getConversationId());
			chat.setStatus(userConversation.getStatus());
			if (!userConversation.getConversation().getConversationName().isEmpty()) {
		        chat.setName(userConversation.getConversation().getConversationName());
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
					contactDTO.setBase64Image( Base64.getEncoder().encodeToString(con.getProfilePhoto()));
					contactDTO.setStatus(con.getStatus());
					contactDTOs.add(contactDTO);
				}
			}
			chat.setContacts(contactDTOs);
			if(chat.getStatus() == ConversationStatus.BLOCKED) {
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
						if(!userConversation.getConversation().getConversationName().isEmpty() &&
								!message.isRead()) {
							List<UserMessageStatus> userMessageStatus = userMessageStatusRepository.findAllByMessage(message);
							List<UserStatusDTO> userStatusDTOs = new ArrayList<>();
							for(UserMessageStatus ums: userMessageStatus) {
								UserStatusDTO userStatusDTO = new UserStatusDTO();
								userStatusDTO.setUserId(ums.getContact().getContactId());
								userStatusDTO.setRead(ums.isRead());
								userStatusDTO.setReadDateTime(ums.getReadDateTime());
								userStatusDTOs.add(userStatusDTO);
							}
							messageDTO.setIsReadInfo(userStatusDTOs);
						}
						messageDTO.setRead(message.isRead());
						messageDTO.setReadDateTime(message.getReadDateTime());
						messageDTO.setSentDateTime(message.getSentDatetime());
						messageDTOs.add(messageDTO);
					} else {
						break;
					}
				}
				chat.setMessages(messageDTOs);
			}
			chats.add(chat);
		}
		return chats;
	}


	public Notification processMessage(MessageFront messageFront) {
	    Notification notification = new Notification();
	    Message message = new Message();
	    MessageDTO messageDTO = new MessageDTO();

	    // Retrieve conversation and sender information
	    Optional<Conversation> conversationOptional = conversationRepository.findById(messageFront.getConversationId());
	    Optional<Contact> senderOptional = contactRepository.findById(messageFront.getSenderId());

	    // Check if conversation and sender exist
	    if (conversationOptional.isEmpty() || senderOptional.isEmpty()) {
	        return null;
	    }

	    Conversation conversation = conversationOptional.get();
	    Contact sender = senderOptional.get();

	    // Check if sender is a participant in the conversation
	    List<Contact> participants = userConversationRepository.findAllByConversation(conversation);
	    if (!participants.contains(sender)) {
	        return null; // Sender is not part of the conversation
	    }

	    if (messageFront.getType() == MessageType.READ) {
	        Optional<Message> messageOptional = messageRepository.findById(messageFront.getMessageId());
	        if (messageOptional.isPresent()) {
	            message = messageOptional.get();
	            if (conversation.getConversationName().isEmpty()) {
	                // If it's a private conversation, mark message as read
	                markMessageAsRead(message, sender);
	            } else {
	                // For group conversation, update user message status
	                updateMessageStatus(message, sender);
	                messageDTO.setRead(true);
	                messageDTO.setReadDateTime(LocalDateTime.now());
	            }
	            messageRepository.save(message);
	            notification.setType(NotificationType.READ);
	        }
	    } else if (messageFront.getType() == MessageType.SEND) {
	        // Prepare message for sending
	        message = prepareMessageForSending(messageFront, sender, conversation);
	        Message savedMessage = messageRepository.save(message);
	        if(!conversation.getConversationName().isEmpty()) {
	        	groupMessagingStatus(sender, savedMessage, conversation);
	        	messageDTO.setRead(false);
                messageDTO.setReadDateTime(null);
	        }
	        notification.setType(NotificationType.SENT);
	    } else if (messageFront.getType() == MessageType.DELETE) {
	        Optional<Message> messageOptional = messageRepository.findById(messageFront.getMessageId());
	        if (messageOptional.isPresent()) {
	            message = messageOptional.get();
	            if(!conversation.getConversationName().isEmpty()) {
		        	deletegroupMessagingStatus(message);
		        	messageDTO.setRead(false);
	                messageDTO.setReadDateTime(null);
		        }
	            messageRepository.delete(message);
	            notification.setType(NotificationType.DELETED);
	        }
	    }

	    // Populate messageDTO
	    messageDTO.setSenderId(message.getContact().getContactId());
	    messageDTO.setMessage(message.getMessageText());
	    messageDTO.setMessageId(message.getMessageId());
	    messageDTO.setSentDateTime(message.getSentDatetime());
	    
	    if(conversation.getConversationName().isEmpty()) {
	    	messageDTO.setRead(message.isRead());
		    messageDTO.setReadDateTime(message.getReadDateTime());
        }
	    

	    // Populate chat data
	    notification.setMessageDTO(messageDTO);
	    notification.setConversationId(messageFront.getConversationId());

	    return notification.getType() != null ? notification : null;
	}

	private void deletegroupMessagingStatus(Message message) {
		List<UserMessageStatus> userMessageStatusList = userMessageStatusRepository.findAllByMessage(message);
		userMessageStatusRepository.deleteAll(userMessageStatusList);
	}


	private void groupMessagingStatus(Contact sender, Message savedMessage, Conversation conversation) {
		List<Contact> participants = userConversationRepository.findAllByConversation(conversation);
		List<UserMessageStatus> userMessageStatuses = new ArrayList<>();
		for(Contact contact: participants) {
			if(contact.getContactId() != sender.getContactId()) {
				UserMessageStatus userMessageStatus = new UserMessageStatus();
				userMessageStatus.setId(new UserMessageStatusId(sender.getContactId(), savedMessage.getMessageId()));
				userMessageStatus.setContact(contact);
				userMessageStatus.setMessage(savedMessage);
				userMessageStatus.setRead(false);
				userMessageStatus.setReadDateTime(null);
				userMessageStatuses.add(userMessageStatus);
			}
		}
		userMessageStatusRepository.saveAll(userMessageStatuses);
	}


	private void markMessageAsRead(Message message, Contact reader) {
	    message.setRead(true);
	    message.setReadDateTime(LocalDateTime.now());
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

	private Message prepareMessageForSending(MessageFront messageFront, Contact sender, Conversation conversation) {
	    Message message = new Message();
	    message.setContact(sender);
	    message.setConversation(conversation);
	    message.setMessageText(messageFront.getMessage());
	    message.setRead(false);
	    message.setSentDatetime(LocalDateTime.now());
	    message.setReadDateTime(null);
	    return message;
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
}
