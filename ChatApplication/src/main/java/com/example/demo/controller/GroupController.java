package com.example.demo.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.GroupCreateDTO;
import com.example.demo.service.GroupService;
import com.example.demo.utils.SanitizationUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/group")
@CrossOrigin
public class GroupController {
	
	@Autowired
	GroupService groupService;
	
	@PostMapping("/new")  
	public ResponseEntity<String> createNewGroup(@Valid @RequestBody GroupCreateDTO groupCreateDTO, HttpServletRequest request) {
		String authorizationHeader = request.getHeader("Authorization");
        String token = authorizationHeader.substring(7);
        try {
			groupService.CreateNewGroup(token, groupCreateDTO);
			return new ResponseEntity<String>("Created Successfully", HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>("Failed", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/photo/{conversationid}")
	public ResponseEntity<?> updatePhoto(@RequestPart("photo") MultipartFile file, HttpServletRequest request, 
			@PathVariable String conversationid) {
		try {
			String authorizationHeader = request.getHeader("Authorization");
	        String token = authorizationHeader.substring(7);
	        String conversationId = SanitizationUtils.sanitizeHtml(conversationid);
	        groupService.updatePhoto(token, file, Long.parseLong(conversationId));
			return new ResponseEntity<String>("Successful", HttpStatus.OK);
		}
		catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Failed to update profile photo.");
        }
		catch(Exception e) {
			return new ResponseEntity<String>("Failed", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/{conversationid}/{contactid}")  
	public ResponseEntity<String> makeUserAsAdmin(@PathVariable String conversationid, @PathVariable String contactid, HttpServletRequest request) {
		String authorizationHeader = request.getHeader("Authorization");
        String token = authorizationHeader.substring(7);
        String conversationId = SanitizationUtils.sanitizeHtml(conversationid);
        String contactId = SanitizationUtils.sanitizeHtml(contactid);
        try {
			groupService.makeAsAdmin(token, Long.parseLong(conversationId), Long.parseLong(contactId));
			return new ResponseEntity<String>("Success", HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>("Failed", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{conversationid}/{contactid}")  
	public ResponseEntity<String> removeUser(@PathVariable String conversationid, @PathVariable String contactid, HttpServletRequest request) {
        try {
        	String authorizationHeader = request.getHeader("Authorization");
            String token = authorizationHeader.substring(7);
            String conversationId = SanitizationUtils.sanitizeHtml(conversationid);
            String contactId = SanitizationUtils.sanitizeHtml(contactid);
			groupService.removeUser(token, Long.parseLong(conversationId), Long.parseLong(contactId), false);
			return new ResponseEntity<String>("Success", HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>("Failed", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/{conversationid}/{contactid}")  
	public ResponseEntity<String> addUser(@PathVariable String conversationid, @PathVariable String contactid, HttpServletRequest request) {
        try {
        	String authorizationHeader = request.getHeader("Authorization");
            String token = authorizationHeader.substring(7);
            String conversationId = SanitizationUtils.sanitizeHtml(conversationid);
            String contactId = SanitizationUtils.sanitizeHtml(contactid);
			groupService.addUser(token, Long.parseLong(conversationId), Long.parseLong(contactId));
			return new ResponseEntity<String>("Success", HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>("Failed", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DeleteMapping("/{conversationid}")  
	public ResponseEntity<String> leaveGroup(@PathVariable String conversationid, HttpServletRequest request) {
        try {
        	String authorizationHeader = request.getHeader("Authorization");
            String token = authorizationHeader.substring(7);
            String conversationId = SanitizationUtils.sanitizeHtml(conversationid);
			groupService.leaveGroup(Long.parseLong(conversationId), token);
			return new ResponseEntity<String>("Success", HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>("Failed", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PutMapping("/mute/{conversationid}")  
	public ResponseEntity<String> mute(@PathVariable String conversationid, HttpServletRequest request) {
        try {
        	String authorizationHeader = request.getHeader("Authorization");
            String token = authorizationHeader.substring(7);
            String conversationId = SanitizationUtils.sanitizeHtml(conversationid);
			groupService.mute(Long.parseLong(conversationId), token);
			return new ResponseEntity<String>("Success", HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>("Failed", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}
