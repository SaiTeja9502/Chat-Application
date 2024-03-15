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

import com.example.demo.dto.FriendDTO;
import com.example.demo.service.ProfileService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/profile")
public class ProfileController {
	
	@Autowired
	ProfileService profileService;
	
	@PutMapping("/username/{userName:[^\\s]{5,15}}")
	public ResponseEntity<?> updateUserName(@PathVariable String userName, HttpServletRequest request) {
		try {
			String authorizationHeader = request.getHeader("Authorization");
	        String token = authorizationHeader.substring(7);
			profileService.updateUserName(userName, token);
			return new ResponseEntity<String>("Successful", HttpStatus.OK);
		}
		catch(Exception e) {
			return new ResponseEntity<String>("Failed", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PutMapping("/photo")
	public ResponseEntity<?> updatePhoto(@RequestPart("photo") MultipartFile file, HttpServletRequest request) {
		try {
			String authorizationHeader = request.getHeader("Authorization");
	        String token = authorizationHeader.substring(7);
			profileService.updatePhoto(token, file);
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
	
	@PutMapping("/phonenumber/{phoneNumber:\\d{10}}")
	public ResponseEntity<?> updatePhoneNumber(@PathVariable String phoneNumber, HttpServletRequest request) {
		try {
			String authorizationHeader = request.getHeader("Authorization");
	        String token = authorizationHeader.substring(7);
			profileService.updatePhoneNumber(phoneNumber, token);
			return new ResponseEntity<String>("Successful", HttpStatus.OK);
		}
		catch(Exception e) {
			return new ResponseEntity<String>("Failed", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/addfriend")
	public ResponseEntity<?> addFriend(@Valid @RequestBody FriendDTO friendDTO, HttpServletRequest request) {
		try {
			String authorizationHeader = request.getHeader("Authorization");
	        String token = authorizationHeader.substring(7);
			profileService.addFriend(token, friendDTO.getPhoneNumber());
			return new ResponseEntity<String>("Successful", HttpStatus.OK);
		}
		catch(Exception e) {
			return new ResponseEntity<String>("Failed", HttpStatus.BAD_REQUEST);
		}
	}
	
	@DeleteMapping("/removefriend")
	public ResponseEntity<?> removeFriend(@Valid @RequestBody FriendDTO friendDTO, HttpServletRequest request) {
		try {
			String authorizationHeader = request.getHeader("Authorization");
	        String token = authorizationHeader.substring(7);
	        profileService.removeFriend(token, friendDTO.getPhoneNumber());
			return new ResponseEntity<String>("Successful", HttpStatus.OK);
		}
		catch(Exception e) {
			return new ResponseEntity<String>("Failed", HttpStatus.BAD_REQUEST);
		}
	}
	
	@PutMapping("/block")
	public ResponseEntity<?> blockFriend(@Valid @RequestBody FriendDTO friendDTO, HttpServletRequest request) {
		try {
			String authorizationHeader = request.getHeader("Authorization");
	        String token = authorizationHeader.substring(7);
			profileService.blockFriend(token, friendDTO.getPhoneNumber());
			return new ResponseEntity<String>("Successful", HttpStatus.OK);
		}
		catch(Exception e) {
			return new ResponseEntity<String>("Failed", HttpStatus.BAD_REQUEST);
		}
	}
	
	@PutMapping("/unblock")
	public ResponseEntity<?> unBlockFriend(@Valid @RequestBody FriendDTO friendDTO, HttpServletRequest request) {
		try {
			String authorizationHeader = request.getHeader("Authorization");
	        String token = authorizationHeader.substring(7);
			profileService.unBlockFriend(token, friendDTO.getPhoneNumber());
			return new ResponseEntity<String>("Successful", HttpStatus.OK);
		}
		catch(Exception e) {
			return new ResponseEntity<String>("Failed", HttpStatus.BAD_REQUEST);
		}
	}
	
	@PutMapping("/mute")
	public ResponseEntity<?> muteFriend(@Valid @RequestBody FriendDTO friendDTO, HttpServletRequest request) {
		try {
			String authorizationHeader = request.getHeader("Authorization");
	        String token = authorizationHeader.substring(7);
			profileService.muteFriend(token, friendDTO.getPhoneNumber());
			return new ResponseEntity<String>("Successful", HttpStatus.OK);
		}
		catch(Exception e) {
			return new ResponseEntity<String>("Failed", HttpStatus.BAD_REQUEST);
		}
	}
}
