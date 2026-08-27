package com.campussync.service;

import com.campussync.entity.User;
import com.campussync.entity.VerificationToken;
import com.campussync.repository.VerificationTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class VerificationTokenService {

    private final VerificationTokenRepository tokenRepository;

    public VerificationTokenService(VerificationTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public String generateToken() {
        return UUID.randomUUID().toString();
    }

    @Transactional
    public void createToken(User user, String token) {
        tokenRepository.deleteByUser(user);
        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(15))
                .build();
        tokenRepository.save(verificationToken);
    }
}
