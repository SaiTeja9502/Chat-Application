package com.example.demo.model;

import java.io.Serializable;
import java.util.Objects;

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
    
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserConversationId that = (UserConversationId) o;
        return Objects.equals(contactId, that.contactId) &&
               Objects.equals(conversationId, that.conversationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contactId, conversationId);
    }
    
}
