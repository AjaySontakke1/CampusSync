package com.campussync.service;

import com.campussync.entity.User;
import com.campussync.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmailVerificationService {

    private final UserRepository userRepository;
    private final VerificationTokenService tokenService;
    private final EmailService emailService;

    public EmailVerificationService(
            UserRepository userRepository,
            VerificationTokenService tokenService,
            EmailService emailService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.emailService = emailService;
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
}
