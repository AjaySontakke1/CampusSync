package com.campussync.service;

import com.campussync.entity.User;
import com.campussync.entity.VerificationToken;
import com.campussync.enums.TokenType;
import com.campussync.repository.VerificationTokenRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class VerificationTokenService {

    private final VerificationTokenRepository tokenRepository;

    public VerificationTokenService(
            VerificationTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public String generateToken() {
        return UUID.randomUUID().toString();
    }

    @Transactional
    public void createToken(
            User user,
            String token,
            TokenType tokenType) {

        tokenRepository.deleteByUserAndTokenType(user, tokenType);

        VerificationToken verificationToken =
                VerificationToken.builder()
                        .token(token)
                        .user(user)
                        .tokenType(tokenType)
                        .expiryDate(
                                LocalDateTime.now().plusMinutes(25))
                        .build();

        tokenRepository.save(verificationToken);
    }
}
