package com.campussync.service;

import com.campussync.dto.SetPasswordRequest;
import com.campussync.entity.User;
import com.campussync.entity.VerificationToken;
import com.campussync.repository.UserRepository;
import com.campussync.repository.VerificationTokenRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class EmailVerificationService {

    private final UserRepository userRepository;
    private final VerificationTokenService tokenService;
    private final EmailService emailService;
    private final VerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    public EmailVerificationService(
            UserRepository userRepository,
            VerificationTokenService tokenService,
            EmailService emailService,
            VerificationTokenRepository tokenRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.emailService = emailService;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void resendVerification(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isEmailVerified()) {
            throw new RuntimeException("Email already verified");
        }

        String token = tokenService.generateToken();
        tokenService.createToken(user, token);

        emailService.sendVerificationEmail(user.getEmail(), token);
    }

    @Transactional
    public void verifyEmail(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);
    }

    @Transactional
    public void setPassword(SetPasswordRequest request) {
        VerificationToken verificationToken = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }

        User user = verificationToken.getUser();

        if (!user.isEmailVerified()) {
            throw new RuntimeException("Email is not verified");
        }

        if (user.getPassword() != null) {
            throw new RuntimeException("Password already set");
        }

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        // Delete token after successful password setup so it cannot be reused
        tokenRepository.delete(verificationToken);
    }
}
