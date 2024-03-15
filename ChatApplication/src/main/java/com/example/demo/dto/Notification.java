package com.example.demo.dto;

import java.util.List;

public class Notification {
	private NotificationType type;
	private Long conversationId;
	private MessageDTO messageDTO;
	private Long senderId;
	private String groupName;
	private List<ContactDTO> contacts;
	
	public Notification() {
		
	}

	public Notification(NotificationType type, Long conversationId, MessageDTO messageDTO, Long senderId,
			String groupName, List<ContactDTO> contacts) {
		super();
		this.type = type;
		this.conversationId = conversationId;
		this.messageDTO = messageDTO;
	}

	public NotificationType getType() {
		return type;
	}

	public void setType(NotificationType type) {
		this.type = type;
	}
	
	public Long getConversationId() {
		return conversationId;
	}

	public void setConversationId(Long conversationId) {
		this.conversationId = conversationId;
	}

	public MessageDTO getMessageDTO() {
		return messageDTO;
	}

	public void setMessageDTO(MessageDTO messageDTO) {
		this.messageDTO = messageDTO;
	}

	public Long getSenderId() {
		return senderId;
	}

	public void setSenderId(Long senderId) {
		this.senderId = senderId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public List<ContactDTO> getContacts() {
		return contacts;
	}

	public void setContacts(List<ContactDTO> contacts) {
		this.contacts = contacts;
	}
	
	

}
