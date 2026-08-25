package com.campussync.controller;

import com.campussync.dto.ParentRegistrationRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/register-parent")
    public ResponseEntity<?> registerParent(
            @Valid @RequestBody ParentRegistrationRequest request) {
        return ResponseEntity.ok("Parent registration request received");
    }
}
