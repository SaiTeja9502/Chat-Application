package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Message;
import com.example.demo.model.UserMessageStatus;
import com.example.demo.model.UserMessageStatusId;

@Repository
public interface UserMessageStatusRepository extends JpaRepository<UserMessageStatus, UserMessageStatusId> {

	List<UserMessageStatus> findAllByMessage(Message message);

}

