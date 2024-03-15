package com.example.demo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_conversation")
public class UserConversation {

    @EmbeddedId
    private UserConversationId id;

    @ManyToOne
    @MapsId("contactId")
    @JoinColumn(name = "contact_id")
    private Contact contact;

    @ManyToOne
    @MapsId("conversationId")
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    @Column(name = "joined_datetime")
    private LocalDateTime joinedDatetime;

    @Column(name = "left_datetime")
    private LocalDateTime leftDatetime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ConversationStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole role;

    // Constructors, getters, and setters
    public UserConversation() {

    }

    public UserConversation(UserConversationId id,Contact contact, Conversation conversation, LocalDateTime joinedDatetime,
            LocalDateTime leftDatetime, ConversationStatus status, UserRole role) {
        super();
        this.id = id;
        this.contact = contact;
        this.conversation = conversation;
        this.joinedDatetime = joinedDatetime;
        this.leftDatetime = leftDatetime;
        this.status = status;
        this.role = role;
    }

    public UserConversationId getId() {
        return id;
    }

    public void setId(UserConversationId id) {
        this.id = id;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public LocalDateTime getJoinedDatetime() {
        return joinedDatetime;
    }

    public void setJoinedDatetime(LocalDateTime joinedDatetime) {
        this.joinedDatetime = joinedDatetime;
    }

    public LocalDateTime getLeftDatetime() {
        return leftDatetime;
    }

    public void setLeftDatetime(LocalDateTime leftDatetime) {
        this.leftDatetime = leftDatetime;
    }

    public ConversationStatus getStatus() {
        return status;
    }

    public void setStatus(ConversationStatus status) {
        this.status = status;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
