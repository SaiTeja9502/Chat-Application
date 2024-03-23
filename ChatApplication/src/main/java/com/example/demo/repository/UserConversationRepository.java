package com.example.demo.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Contact;
import com.example.demo.model.Conversation;
import com.example.demo.model.UserConversation;
import com.example.demo.model.UserConversationId;

@Repository
public interface UserConversationRepository extends JpaRepository<UserConversation, UserConversationId> {
    
    @Query("SELECT uc FROM UserConversation uc WHERE uc.contact = :contact1 " +
               "AND uc.conversation IN (SELECT uc2.conversation FROM UserConversation uc2 WHERE uc2.contact = :contact2)" +
               "AND uc.conversation.conversationName IS NULL")
    List<UserConversation> findCommonConversationsWithNullName(@Param("contact1") Contact contact1, @Param("contact2") Contact contact2);
    
    @Query(value = "SELECT * FROM user_conversation WHERE contact_id = :contactId", nativeQuery = true)
    List<UserConversation> findAllByContactId(@Param("contactId") Long contactId);

    @Query("SELECT uc.contact FROM UserConversation uc WHERE uc.conversation = :conversation")
    List<Contact> findAllByConversation(@Param("conversation") Conversation conversation);
}


