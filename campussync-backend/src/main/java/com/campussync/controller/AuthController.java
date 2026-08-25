package com.campussync.controller;

import com.campussync.dto.ParentRegistrationRequest;
import com.campussync.dto.StudentRegistrationRequest;
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

    public AuthController(
            ParentRegistrationService parentRegistrationService,
            StudentRegistrationService studentRegistrationService) {
        this.parentRegistrationService = parentRegistrationService;
        this.studentRegistrationService = studentRegistrationService;
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
}
