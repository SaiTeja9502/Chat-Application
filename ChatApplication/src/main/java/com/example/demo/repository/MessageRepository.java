package com.example.demo.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Contact;
import com.example.demo.model.Conversation;
import com.example.demo.model.Message;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
	void deleteByConversation(Conversation conversation);

	List<Message> findByContactAndConversation(Contact contact, Conversation conversation);

	@Query("SELECT m FROM Message m WHERE m.conversation = :conversation ORDER BY m.sentDatetime ASC")
    Page<Message> findMessagesByConversation(@Param("conversation") Conversation conversation, Pageable pageable);
}


