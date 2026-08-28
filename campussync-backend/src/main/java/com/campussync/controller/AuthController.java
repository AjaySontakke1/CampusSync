package com.campussync.controller;

import com.campussync.dto.ParentRegistrationRequest;
import com.campussync.dto.StudentRegistrationRequest;
import com.campussync.dto.SetPasswordRequest;
import com.campussync.dto.LoginRequest;
import com.campussync.dto.LoginResponse;
import com.campussync.service.AuthService;
import com.campussync.service.EmailVerificationService;
import com.campussync.service.ParentRegistrationService;
import com.campussync.service.StudentRegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final ParentRegistrationService parentRegistrationService;
    private final StudentRegistrationService studentRegistrationService;
    private final EmailVerificationService emailVerificationService;
    private final AuthService authService;

    public AuthController(
            ParentRegistrationService parentRegistrationService,
            StudentRegistrationService studentRegistrationService,
            EmailVerificationService emailVerificationService,
            AuthService authService) {
        this.parentRegistrationService = parentRegistrationService;
        this.studentRegistrationService = studentRegistrationService;
        this.emailVerificationService = emailVerificationService;
        this.authService = authService;
    }

    @PostMapping("/register-parent")
    public ResponseEntity<?> registerParent(
            @Valid @RequestBody ParentRegistrationRequest request) {
        parentRegistrationService.registerParent(request);
        return ResponseEntity.ok("Parent registration request received");
    }

    @PostMapping("/register-student")
    public ResponseEntity<?> registerStudent(
            @Valid @RequestBody StudentRegistrationRequest request) {
        studentRegistrationService.registerStudent(request);
        return ResponseEntity.ok("Student registered successfully");
    }

    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        emailVerificationService.verifyEmail(token);
        return ResponseEntity.ok("Email verified successfully");
    }

    @PostMapping("/set-password")
    public ResponseEntity<String> setPassword(
            @Valid @RequestBody SetPasswordRequest request) {
        emailVerificationService.setPassword(request);
        return ResponseEntity.ok("Password set successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
