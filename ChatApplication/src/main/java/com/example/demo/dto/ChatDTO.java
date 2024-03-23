package com.example.demo.dto;

import java.util.List;

import com.example.demo.model.Contact;
import com.example.demo.model.ConversationStatus;
import com.example.demo.model.UserRole;

public class ChatDTO {
	private Long conversationId;
	private String name;
	private String groupProfile;
	private ConversationStatus status;
	private UserRole userRole;
	private List<ContactDTO> contacts;
	private List<MessageDTO> messages;
	
	public ChatDTO() {
		
	}

	public ChatDTO(Long conversationId, String name, String groupProfile, ConversationStatus status, UserRole userRole, List<ContactDTO> contacts, List<MessageDTO> messages) {
		super();
		this.conversationId = conversationId;
		this.name = name;
		this.status = status;
		this.groupProfile = groupProfile;
		this.userRole = userRole;
		this.contacts = contacts;
		this.messages = messages;
	}

	public Long getConversationId() {
		return conversationId;
	}

	public void setConversationId(Long conversationId) {
		this.conversationId = conversationId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getGroupProfile() {
		return groupProfile;
	}

	public void setGroupProfile(String groupProfile) {
		this.groupProfile = groupProfile;
	}

	public UserRole getUserRole() {
		return userRole;
	}

	public void setUserRole(UserRole userRole) {
		this.userRole = userRole;
	}

	public ConversationStatus getStatus() {
		return status;
	}

	public void setStatus(ConversationStatus status) {
		this.status = status;
	}

	public List<ContactDTO> getContacts() {
		return contacts;
	}

	public void setContacts(List<ContactDTO> contacts) {
		this.contacts = contacts;
	}

	public List<MessageDTO> getMessages() {
		return messages;
	}

	public void setMessages(List<MessageDTO> messages) {
		this.messages = messages;
	}
	
}
