package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Conversation;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    // You can define additional methods here if needed
}

