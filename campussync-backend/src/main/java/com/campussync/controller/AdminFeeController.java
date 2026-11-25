package com.campussync.controller;

import com.campussync.dto.request.FeeConcessionDecisionDTO;
import com.campussync.dto.response.FeeConcessionResponseDTO;
import com.campussync.service.FeeConcessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/fees")
@RequiredArgsConstructor
public class AdminFeeController {

    private final FeeConcessionService feeConcessionService;

    @GetMapping("/concessions/pending")
    public ResponseEntity<List<FeeConcessionResponseDTO>> getPendingConcessions() {

        return ResponseEntity.ok(
                feeConcessionService.getPendingRequests()
        );
    }

    @PutMapping("/concessions/{id}/decision")
    public ResponseEntity<FeeConcessionResponseDTO> decideConcession(
            @PathVariable Long id,
            @Valid @RequestBody FeeConcessionDecisionDTO request,
            Authentication authentication) {

        return ResponseEntity.ok(
                feeConcessionService.decideConcession(
                        id,
                        request,
                        authentication.getName()
                )
        );
    }
}
