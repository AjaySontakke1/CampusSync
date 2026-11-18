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

    // 1. Assign subject
    @PostMapping
    public ResponseEntity<SemesterSubjectAssignmentResponse> assignSubject(
            @PathVariable Long offeringId,
            @Valid @RequestBody SemesterSubjectAssignmentRequest request) {

        SemesterSubjectAssignmentResponse response =
                assignmentService.assignSubject(
                        offeringId,
                        request.getSubjectId()
                );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 2. Get all subject assignments
    @GetMapping
    public ResponseEntity<List<SemesterSubjectAssignmentResponse>>
            getAllAssignments(@PathVariable Long offeringId) {

        List<SemesterSubjectAssignmentResponse> response =
                assignmentService.getAssignmentsByOffering(offeringId);

        return ResponseEntity.ok(response);
    }

    // 3. Get active subject assignments
    @GetMapping("/active")
    public ResponseEntity<List<SemesterSubjectAssignmentResponse>>
            getActiveAssignments(@PathVariable Long offeringId) {

        List<SemesterSubjectAssignmentResponse> response =
                assignmentService.getActiveAssignments(offeringId);

        return ResponseEntity.ok(response);
    }

    // 4. Deactivate subject assignment
    @PatchMapping("/{assignmentId}/deactivate")
    public ResponseEntity<SemesterSubjectAssignmentResponse>
            deactivateAssignment(
                    @PathVariable Long offeringId,
                    @PathVariable Long assignmentId) {

        SemesterSubjectAssignmentResponse response =
                assignmentService.deactivateAssignment(assignmentId);

        // Ensure assignment belongs to the offering in the URL
        if (!response.getSemesterOfferingId().equals(offeringId)) {
            throw new IllegalArgumentException(
                    "Assignment does not belong to this semester offering");
        }

        return ResponseEntity.ok(response);
    }
}
