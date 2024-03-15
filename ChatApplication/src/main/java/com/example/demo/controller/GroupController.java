package com.example.demo.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/group")
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
	
	@PutMapping("/photo/{conversationId}")
	public ResponseEntity<?> updatePhoto(@RequestPart("photo") MultipartFile file, HttpServletRequest request, 
			@PathVariable Long conversationId) {
		try {
			String authorizationHeader = request.getHeader("Authorization");
	        String token = authorizationHeader.substring(7);
	        groupService.updatePhoto(token, file, conversationId);
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
	
	@PutMapping("/{conversationId}/{contactId}")  
	public ResponseEntity<String> makeUserAsAdmin(@PathVariable Long conversationId, @PathVariable Long contactId, HttpServletRequest request) {
		String authorizationHeader = request.getHeader("Authorization");
        String token = authorizationHeader.substring(7);
        try {
			groupService.makeAsAdmin(token, conversationId, contactId);
			return new ResponseEntity<String>("Success", HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>("Failed", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DeleteMapping("/{conversationId}/{contactId}")  
	public ResponseEntity<String> removeUser(@PathVariable Long conversationId, @PathVariable Long contactId) {
        try {
			groupService.removeUser(conversationId, contactId);
			return new ResponseEntity<String>("Success", HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>("Failed", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/{conversationId}/{contactId}")  
	public ResponseEntity<String> addUser(@PathVariable Long conversationId, @PathVariable Long contactId) {
        try {
			groupService.addUser(conversationId, contactId);
			return new ResponseEntity<String>("Success", HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>("Failed", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DeleteMapping("/{conversationId}/leave")  
	public ResponseEntity<String> leaveGroup(@PathVariable Long conversationId, HttpServletRequest request) {
        try {
        	String authorizationHeader = request.getHeader("Authorization");
            String token = authorizationHeader.substring(7);
			groupService.leaveGroup(conversationId, token);
			return new ResponseEntity<String>("Success", HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>("Failed", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PutMapping("/{conversationId}/mute")  
	public ResponseEntity<String> mute(@PathVariable Long conversationId, HttpServletRequest request) {
        try {
        	String authorizationHeader = request.getHeader("Authorization");
            String token = authorizationHeader.substring(7);
			groupService.mute(conversationId, token);
			return new ResponseEntity<String>("Success", HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>("Failed", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}
