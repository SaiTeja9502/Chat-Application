package com.example.demo.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;



@Embeddable
public class UserConversationId implements Serializable {
	
	private static final long serialVersionUID = 1L;

    @Column(name = "contact_id")
    private Long contactId;

    @Column(name = "conversation_id")
    private Long conversationId;

    // Constructors, getters, and setters
    public UserConversationId() {
    	
    }

	public UserConversationId(Long contactId, Long conversationId) {
		super();
		this.contactId = contactId;
		this.conversationId = conversationId;
	}

	public Long getContactId() {
		return contactId;
	}

	public void setContactId(Long contactId) {
		this.contactId = contactId;
	}

	public Long getConversationId() {
		return conversationId;
	}

	public void setConversationId(Long conversationId) {
		this.conversationId = conversationId;
	}
    
    
}
