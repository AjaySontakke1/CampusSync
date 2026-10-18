package com.campussync.service;

import com.campussync.dto.ResetPasswordRequest;
import com.campussync.dto.SetPasswordRequest;
import com.campussync.entity.User;
import com.campussync.entity.VerificationToken;
import com.campussync.enums.TokenType;
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
    private final RefreshTokenService refreshTokenService;

    public EmailVerificationService(
            UserRepository userRepository,
            VerificationTokenService tokenService,
            EmailService emailService,
            VerificationTokenRepository tokenRepository,
            PasswordEncoder passwordEncoder,
            RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.emailService = emailService;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional
    public void resendVerification(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        if (user.isEmailVerified()) {
            throw new IllegalArgumentException(
                    "Email is already verified");
        }

        // Generate a new verification token.
        String token = tokenService.generateToken();

        // Save it as an email-verification token.
        tokenService.createToken(
                user,
                token,
                TokenType.EMAIL_VERIFICATION);

        // Send the verification link.
        emailService.sendVerificationEmail(
                user.getEmail(),
                token);
    }

    @Transactional
    public void forgotPassword(String email) {

        // Find the account associated with this email
        var optionalUser = userRepository.findByEmail(email);

        // Always return without revealing whether the account exists
        if (optionalUser.isEmpty()) {
            return;
        }

        var user = optionalUser.get();

        // Do not reset passwords for unverified or inactive accounts
        if (!user.isEmailVerified()
                || !Boolean.TRUE.equals(user.getActive())) {
            return;
        }

        // Generate and store a password-reset token
        String token = tokenService.generateToken();

        tokenService.createToken(
                user,
                token,
                TokenType.PASSWORD_RESET
        );

        // Prepare the reset email
        emailService.sendPasswordResetEmail(
                user.getEmail(),
                token
        );
    }

    @Transactional
    public void verifyEmail(String token) {

        VerificationToken verificationToken =
                tokenRepository.findByToken(token)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Invalid verification token"));

        // Accept only email-verification tokens.
        if (verificationToken.getTokenType()
                != TokenType.EMAIL_VERIFICATION) {
            throw new IllegalArgumentException(
                    "Invalid email-verification token type");
        }

        // Check token expiry.
        if (!verificationToken.getExpiryDate()
                .isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "Verification token has expired");
        }

        User user = verificationToken.getUser();

        // Mark the email as verified.
        user.setEmailVerified(true);
        userRepository.save(user);

        // Remove the email-verification token.
        tokenRepository.delete(verificationToken);

        // Generate a separate password-setup token.
        String passwordToken = tokenService.generateToken();

        tokenService.createToken(
                user,
                passwordToken,
                TokenType.PASSWORD_SETUP);

        // Send the password-setup email.
        emailService.sendPasswordSetupEmail(
                user.getEmail(),
                passwordToken);
    }

    @Transactional
    public void setPassword(SetPasswordRequest request) {

        VerificationToken verificationToken =
                tokenRepository.findByToken(request.getToken())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Invalid password setup token"));

        // Accept only password-setup tokens.
        if (verificationToken.getTokenType()
                != TokenType.PASSWORD_SETUP) {
            throw new IllegalArgumentException(
                    "Invalid password setup token type");
        }

        // Check expiry.
        if (!verificationToken.getExpiryDate()
                .isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "Password setup token has expired");
        }

        User user = verificationToken.getUser();

        // Email must be verified first.
        if (!user.isEmailVerified()) {
            throw new IllegalArgumentException(
                    "Please verify your email first");
        }

        // Prevent changing an already-set password through this flow.
        if (user.getPassword() != null) {
            throw new IllegalArgumentException(
                    "Password has already been set");
        }

        // Store a hashed password, never the plain-text password.
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        // Make the token single-use.
        tokenRepository.delete(verificationToken);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {

        // Find the reset token
        VerificationToken verificationToken =
                tokenRepository.findByToken(request.getToken())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Invalid password reset token"));

        // Ensure this token is only for password reset
        if (verificationToken.getTokenType()
                != TokenType.PASSWORD_RESET) {
            throw new IllegalArgumentException(
                    "Invalid password reset token");
        }

        // Check whether the token has expired
        if (!verificationToken.getExpiryDate()
                .isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "Password reset token has expired");
        }

        User user = verificationToken.getUser();

        // Only verified, active accounts can reset passwords
        if (!user.isEmailVerified()
                || !Boolean.TRUE.equals(user.getActive())) {
            throw new IllegalArgumentException(
                    "Account is not eligible for password reset");
        }

        // 1. Save the new password securely
        user.setPassword(
                passwordEncoder.encode(request.getNewPassword())
        );

        userRepository.save(user);

        // 2. Revoke all active refresh-token sessions
        refreshTokenService.revokeAllUserRefreshTokens(user);

        // 3. Delete the password-reset token
        tokenRepository.delete(verificationToken);
    }
}
