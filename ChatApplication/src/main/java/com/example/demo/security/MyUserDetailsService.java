package com.example.demo.security;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.example.demo.model.Contact;
import com.example.demo.repository.ContactRepository;

@Component
public class MyUserDetailsService implements UserDetailsService{

	
	@Autowired
	ContactRepository contactRepository;
	
	@Override
	public UserDetails loadUserByUsername(String contactId) throws UsernameNotFoundException {
		Optional<Contact> contact = contactRepository.findById(Long.parseLong(contactId));
		
		contact.orElseThrow(()-> new UsernameNotFoundException("Not Found: " + contactId));
		
		return contact.map(MyUserDetails::new).get();
	}

}
