package com.campussync.service;

import com.campussync.dto.ParentRegistrationRequest;
import com.campussync.entity.Parent;
import com.campussync.entity.User;
import com.campussync.enums.Role;
import com.campussync.enums.TokenType;
import com.campussync.repository.ParentRepository;
import com.campussync.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ParentRegistrationService {

    private final UserRepository userRepository;
    private final ParentRepository parentRepository;
    private final VerificationTokenService tokenService;
    private final EmailService emailService;

    public ParentRegistrationService(
            UserRepository userRepository,
            ParentRepository parentRepository,
            VerificationTokenService tokenService,
            EmailService emailService) {
        this.userRepository = userRepository;
        this.parentRepository = parentRepository;
        this.tokenService = tokenService;
        this.emailService = emailService;
    }

    @Transactional
    public void registerParent(ParentRegistrationRequest request) {

        // 1. Check whether the email is already registered.
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException(
                    "Email is already registered");
        }

        // 2. Create the user account.
        User user = new User();
        user.setUsername(request.getEmail());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(Role.PARENT);
        user.setEmailVerified(false);
        user.setActive(true);

        User savedUser = userRepository.save(user);

        // 3. Create the parent profile.
        Parent parent = new Parent();
        parent.setFirstName(request.getFirstName());
        parent.setLastName(request.getLastName());
        parent.setPhone(request.getPhone());
        parent.setUser(savedUser);

        parentRepository.save(parent);

        // 4. Generate and save the verification token.
        String token = tokenService.generateToken();

        tokenService.createToken(
                savedUser,
                token,
                TokenType.EMAIL_VERIFICATION);

        // 5. Send the verification link.
        emailService.sendVerificationEmail(
                savedUser.getEmail(),
                token);
    }
}
