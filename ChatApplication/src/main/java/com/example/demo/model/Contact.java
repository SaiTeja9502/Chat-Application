package com.example.demo.model;

import java.util.Arrays;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "contact")
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contact_id")
    private Long contactId;

    @Column(name = "username")
    private String userName;

    @Lob
    @Column(name = "profile_picture")
    private byte[] profilePhoto;

    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Column(name = "password")
    private String password;
    
    @Column(name = "status")
    private UserStatus status;
    
    @OneToOne
    @JoinColumn(name = "security_id")
    private Security security;

    // Constructors, getters, and setters
    public Contact() {
    	
    }

	public Contact(Long contactId, String userName, byte[] profilePhoto, String phoneNumber, String password, Security security) {
		this.contactId = contactId;
		this.userName = userName;
		this.profilePhoto = profilePhoto;
		this.phoneNumber = phoneNumber;
		this.password = password;
		this.security = security;
		this.status = UserStatus.OFFLINE;
	}

	public Long getContactId() {
		return contactId;
	}

	public void setContactId(Long contactId) {
		this.contactId = contactId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public byte[] getProfilePhoto() {
		return profilePhoto;
	}

	public void setProfilePhoto(byte[] profilePhoto) {
		this.profilePhoto = profilePhoto;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Security getSecurity() {
		return security;
	}

	public void setSecurity(Security security) {
		this.security = security;
	}

	public UserStatus getStatus() {
		return status;
	}

	public void setStatus(UserStatus status) {
		this.status = status;
	}
	
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact contact = (Contact) o;
        return Objects.equals(contactId, contact.contactId) &&
                Objects.equals(userName, contact.userName) &&
                Arrays.equals(profilePhoto, contact.profilePhoto) &&
                Objects.equals(phoneNumber, contact.phoneNumber) &&
                Objects.equals(password, contact.password) &&
                status == contact.status &&
                Objects.equals(security, contact.security);
    }
}

