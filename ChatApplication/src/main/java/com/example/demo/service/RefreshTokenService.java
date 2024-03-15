package com.example.demo.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.exceptions.InvalidRefreshTokenException;
import com.example.demo.model.RefreshToken;
import com.example.demo.repository.ContactRepository;
import com.example.demo.repository.RefreshTokenRepository;

@Service
public class RefreshTokenService {

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    ContactRepository contactRepository;

    public RefreshToken createRefreshToken(String phoneNumber){
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setContact(contactRepository.findByPhoneNumber(phoneNumber).get());
        refreshToken.setExpiryDate(Instant.now().plusMillis(600000));
        refreshToken.setToken(UUID.randomUUID().toString());
        return refreshTokenRepository.save(refreshToken);
    }



    public Optional<RefreshToken> findByToken(String token){
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token){
        if(token.getExpiryDate().compareTo(Instant.now())<0){
            refreshTokenRepository.delete(token);
            System.out.println("Hello world");
            throw new InvalidRefreshTokenException(token.getToken() + " Refresh token is expired. Please make a new login..!");
        }
        return token;
    }

}
