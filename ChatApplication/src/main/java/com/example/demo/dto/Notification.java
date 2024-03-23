package com.example.demo.dto;


public class Notification {
	private NotificationType type;
	private Long conversationId;
	private MessageDTO messageDTO;
	private Long senderId;
	private String groupName;
	private ContactDTO contact;
	
	public Notification() {
		
	}

	public Notification(NotificationType type, Long conversationId, MessageDTO messageDTO, Long senderId,
			String groupName, ContactDTO contact) {
		super();
		this.type = type;
		this.conversationId = conversationId;
		this.messageDTO = messageDTO;
		this.senderId = senderId;
		this.groupName = groupName;
		this.contact = contact;
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

	public ContactDTO getContact() {
		return contact;
	}

	public void setContact(ContactDTO contacts) {
		this.contact = contacts;
	}
	
	

}
