package com.example.demo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_message_status")
public class UserMessageStatus {

    @EmbeddedId
    private UserMessageStatusId id;
    
    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "contact_id")
    private Contact contact;

    @ManyToOne
    @MapsId("messageId")
    @JoinColumn(name = "message_id")
    private Message message;

    @Column(name = "is_read")
    private boolean isRead;
    
    @Column(name = "read_datetime")
    private LocalDateTime readDateTime;
    
    @Column(name = "is_delivered")
    private boolean isDelivered;
    
    @Column(name = "delivered_datetime")
    private LocalDateTime deliveredDateTime;

    // Constructors, getters, and setters
    public UserMessageStatus() {
    	
    }

	public UserMessageStatus(UserMessageStatusId id, Contact contact, Message message, boolean isRead, 
			LocalDateTime readDateTime, boolean isDelivered, LocalDateTime deliveredDateTime) {
		super();
		this.id = id;
		this.contact = contact;
		this.message = message;
		this.isRead = isRead;
		this.readDateTime = readDateTime;
		this.isDelivered = isDelivered;
		this.deliveredDateTime = deliveredDateTime;
	}

	public UserMessageStatusId getId() {
		return id;
	}

	public void setId(UserMessageStatusId id) {
		this.id = id;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}

	public LocalDateTime getReadDateTime() {
		return readDateTime;
	}

	public void setReadDateTime(LocalDateTime readDateTime) {
		this.readDateTime = readDateTime;
	}

	public boolean isDelivered() {
		return isDelivered;
	}

	public void setDelivered(boolean isDelivered) {
		this.isDelivered = isDelivered;
	}

	public LocalDateTime getDeliveredDateTime() {
		return deliveredDateTime;
	}

	public void setDeliveredDateTime(LocalDateTime deliveredDateTime) {
		this.deliveredDateTime = deliveredDateTime;
	}
    
	
    
}

