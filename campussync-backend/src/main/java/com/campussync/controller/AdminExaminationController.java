package com.campussync.controller;

import com.campussync.dto.request.ExaminationSessionRequestDTO;
import com.campussync.dto.response.ExaminationSessionResponseDTO;
import com.campussync.service.ExaminationSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/examinations")
@RequiredArgsConstructor
public class AdminExaminationController {

    private final ExaminationSessionService sessionService;

    @PostMapping("/sessions")
    public ResponseEntity<ExaminationSessionResponseDTO> createSession(
            @Valid @RequestBody ExaminationSessionRequestDTO request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sessionService.createSession(request));
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<ExaminationSessionResponseDTO>> getSessions() {

        return ResponseEntity.ok(
                sessionService.getAllSessions());
    }

    @PutMapping("/sessions/{id}/open")
    public ResponseEntity<ExaminationSessionResponseDTO> openSession(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                sessionService.openSession(id));
    }

    @PutMapping("/sessions/{id}/close")
    public ResponseEntity<ExaminationSessionResponseDTO> closeSession(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                sessionService.closeSession(id));
    }

    @PutMapping("/sessions/{id}/complete")
    public ResponseEntity<ExaminationSessionResponseDTO> completeSession(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                sessionService.completeSession(id));
    }
}
