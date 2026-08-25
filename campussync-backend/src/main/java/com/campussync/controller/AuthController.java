package com.campussync.controller;

import com.campussync.dto.ParentRegistrationRequest;
import com.campussync.service.ParentRegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final ParentRegistrationService parentRegistrationService;

    public AuthController(ParentRegistrationService parentRegistrationService) {
        this.parentRegistrationService = parentRegistrationService;
    }

    @PostMapping("/register-parent")
    public ResponseEntity<?> registerParent(
            @Valid @RequestBody ParentRegistrationRequest request) {
        parentRegistrationService.registerParent(request);
        return ResponseEntity.ok("Parent registration request received");
    }
}
