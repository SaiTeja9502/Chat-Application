package com.example.demo.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Contact;
import com.example.demo.model.Conversation;
import com.example.demo.model.UserConversation;
import com.example.demo.model.UserConversationId;

@Repository
public interface UserConversationRepository extends JpaRepository<UserConversation, UserConversationId> {
	
	@Query("SELECT uc.conversation FROM UserConversation uc " +
	           "WHERE uc.contact.id = :contactId1 AND uc.conversation IN " +
	           "(SELECT uc2.conversation FROM UserConversation uc2 WHERE uc2.contact.id = :contactId2) " +
	           "AND uc.conversation.name IS NULL")
	Optional<Conversation> findCommonConversationByContacts(Long contactId1, Long contactId2);

	List<UserConversation> findAllByContact(Contact contact);

	List<Contact> findAllByConversation(Conversation conversation);
}

