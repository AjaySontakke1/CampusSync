package com.campussync.controller;

import com.campussync.dto.RefreshTokenRequest;
import com.campussync.dto.RefreshTokenResponse;
import com.campussync.service.RefreshTokenService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class RefreshTokenController {

    private final RefreshTokenService refreshTokenService;

    public RefreshTokenController(
            RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {

        try {
            String newAccessToken =
                    refreshTokenService.refreshAccessToken(
                            request.getRefreshToken());

            RefreshTokenResponse response =
                    RefreshTokenResponse.builder()
                            .accessToken(newAccessToken)
                            .build();

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid or expired refresh token");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Valid @RequestBody RefreshTokenRequest request) {

        try {
            refreshTokenService.revokeRefreshToken(
                    request.getRefreshToken());

            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid refresh token");
        }
    }
}
