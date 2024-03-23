package com.example.demo.model;




import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long messageId;
    
    @ManyToOne
    @JoinColumn(name = "contact_id")
    private Contact contact;

    @Column(name = "message_text")
    private String messageText;

    @Column(name = "sent_datetime")
    private LocalDateTime sentDatetime;
    
    @Column(name = "is_read")
    private boolean isRead;
    
    @Column(name = "read_datetime")
    private LocalDateTime readDateTime;
    
    @Column(name = "is_delivered")
    private boolean isDelivered;
    
    @Column(name = "delivered_datetime")
    private LocalDateTime deliveredDateTime;

    @ManyToOne
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;
    
    public Message() {
    	
    }
    
	public Message(Long messageId, Contact contact, String messageText, LocalDateTime sentDatetime,
			boolean isRead, LocalDateTime readDateTime, boolean isDelivered, LocalDateTime deliveredDateTime, Conversation conversation) {
		super();
		this.messageId = messageId;
		this.contact = contact;
		this.messageText = messageText;
		this.sentDatetime = sentDatetime;
		this.isRead = isRead;
		this.readDateTime = readDateTime;
		this.isDelivered = isDelivered;
		this.deliveredDateTime = deliveredDateTime;
		this.conversation = conversation;
	}

	public Long getMessageId() {
		return messageId;
	}

	public void setMessageId(Long messageId) {
		this.messageId = messageId;
	}

	

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public String getMessageText() {
		return messageText;
	}

	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}

	public LocalDateTime getSentDatetime() {
		return sentDatetime;
	}

	public void setSentDatetime(LocalDateTime sentDatetime) {
		this.sentDatetime = sentDatetime;
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

	public Conversation getConversation() {
		return conversation;
	}

	public void setConversation(Conversation conversation) {
		this.conversation = conversation;
	}
    
}

