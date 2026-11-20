package com.campussync.controller.admin;

import com.campussync.dto.request.SemesterSubjectAssignmentRequest;
import com.campussync.dto.response.SemesterSubjectAssignmentResponse;
import com.campussync.service.SemesterSubjectAssignmentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/semester-offerings/{offeringId}/subjects")
@RequiredArgsConstructor
public class AdminSemesterSubjectController {

    private final SemesterSubjectAssignmentService assignmentService;

    // 1. Assign subject to semester offering
    @PostMapping
    public ResponseEntity<SemesterSubjectAssignmentResponse> assignSubject(
            @PathVariable Long offeringId,
            @Valid @RequestBody SemesterSubjectAssignmentRequest request) {

        // Ensure the request offering matches the URL offering if provided
        if (request.getSemesterOfferingId() != null && !offeringId.equals(request.getSemesterOfferingId())) {
            throw new IllegalArgumentException(
                    "Offering ID in URL and request body must match"
            );
        }

        request.setSemesterOfferingId(offeringId);

        SemesterSubjectAssignmentResponse response =
                assignmentService.assignSubject(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 2. Get all subject assignments
    @GetMapping
    public ResponseEntity<List<SemesterSubjectAssignmentResponse>>
    getAssignments(@PathVariable Long offeringId) {

        return ResponseEntity.ok(
                assignmentService.getAssignmentsByOffering(offeringId)
        );
    }

    // 3. Get only active subject assignments
    @GetMapping("/active")
    public ResponseEntity<List<SemesterSubjectAssignmentResponse>>
    getActiveAssignments(@PathVariable Long offeringId) {

        return ResponseEntity.ok(
                assignmentService.getActiveAssignments(offeringId)
        );
    }

    // 4. Deactivate subject assignment
    @PatchMapping("/{assignmentId}/deactivate")
    public ResponseEntity<Void> deactivateAssignment(
            @PathVariable Long offeringId,
            @PathVariable Long assignmentId) {

        assignmentService.deactivateAssignment(
                offeringId,
                assignmentId
        );

        return ResponseEntity.noContent().build();
    }
}
