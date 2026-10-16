package com.campussync.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campussync.dto.RefreshTokenResponse;
import com.campussync.entity.RefreshToken;
import com.campussync.entity.User;
import com.campussync.repository.RefreshTokenRepository;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final SecureRandom secureRandom = new SecureRandom();
    private final long expirationMs;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            JwtService jwtService,
            @Value("${refresh-token.expiration-ms:604800000}")
            long expirationMs) {

        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
        this.expirationMs = expirationMs;
    }

    @Transactional
    public RefreshTokenResponse rotateRefreshToken(String rawToken) {

        if (rawToken == null || rawToken.isBlank()) {
            throw new IllegalArgumentException(
                    "Refresh token is required");
        }

        String tokenHash = hashToken(rawToken);

        RefreshToken oldToken = refreshTokenRepository
                .findByTokenHashForUpdate(tokenHash)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid refresh token"));

        if (oldToken.isRevoked()) {
            throw new IllegalArgumentException(
                    "Refresh token has been revoked");
        }

        if (!oldToken.getExpiryDate().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "Refresh token has expired");
        }

        User user = oldToken.getUser();

        if (!Boolean.TRUE.equals(user.getActive())
                || !user.isEmailVerified()) {
            throw new IllegalArgumentException(
                    "User is not allowed to log in");
        }

        // Revoke the old refresh token.
        oldToken.setRevoked(true);
        refreshTokenRepository.save(oldToken);

        // Generate both replacement tokens.
        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = createRefreshToken(user);

        return RefreshTokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    @Transactional
    public String createRefreshToken(User user) {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);

        String rawToken =
                Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString(randomBytes);

        RefreshToken refreshToken = RefreshToken.builder()
                .tokenHash(hashToken(rawToken))
                .user(user)
                .expiryDate(
                        LocalDateTime.now()
                                .plusNanos(expirationMs * 1_000_000L))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);

        return rawToken;
    }

    @Transactional
    public void revokeRefreshToken(String rawToken) {

        if (rawToken == null || rawToken.isBlank()) {
            throw new IllegalArgumentException(
                    "Refresh token is required");
        }

        String tokenHash = hashToken(rawToken);

        RefreshToken refreshToken = refreshTokenRepository
                .findByTokenHashForUpdate(tokenHash)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid refresh token"));

        if (refreshToken.isRevoked()) {
            throw new IllegalArgumentException(
                    "Refresh token has already been revoked");
        }

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder()
                    .encodeToString(hash);

        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(
                    "SHA-256 is not available", ex);
        }
    }
}
