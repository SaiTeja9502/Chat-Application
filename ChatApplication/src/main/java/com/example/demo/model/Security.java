package com.example.demo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "security")
public class Security {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "security_id")
    private Long securityId;
	
	private String question;
	
	private String answer;
	
	private AccountStatus accountStatus;
	
	private int tries;
	
	private LocalDateTime lockedLocalDateTime;
	
	public Security() {
		
	}

	public Security(Long securityId, String question, String answer) {
		super();
		this.securityId = securityId;
		this.question = question;
		this.answer = answer;
		this.accountStatus = AccountStatus.OK;
		this.tries = 0;
		this.lockedLocalDateTime = null;
	}

	public Long getSecurityId() {
		return securityId;
	}

	public void setSecurityId(Long securityId) {
		this.securityId = securityId;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public boolean isLocked() {
	    return this.accountStatus == AccountStatus.LOCKED;
	}


	public void setAccountStatus(AccountStatus accountStatus) {
		this.accountStatus = accountStatus;
	}

	public int getTries() {
		return tries;
	}

	public void setTries(int tries) {
		this.tries = tries;
	}

	public LocalDateTime getLockedLocalDateTime() {
		return lockedLocalDateTime;
	}

	public void setLockedLocalDateTime(LocalDateTime lockedLocalDateTime) {
		this.lockedLocalDateTime = lockedLocalDateTime;
	}
	
	
}
