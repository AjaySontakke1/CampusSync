package com.campussync.controller.admin;

import com.campussync.dto.request.SemesterOfferingRequest;
import com.campussync.dto.request.SemesterOfferingUpdateRequest;
import com.campussync.dto.response.SemesterOfferingResponse;
import com.campussync.enums.SemesterStatus;
import com.campussync.service.SemesterOfferingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/semester-offerings")
@RequiredArgsConstructor
public class AdminSemesterOfferingController {

    private final SemesterOfferingService offeringService;

    // 1. Create a semester offering
    @PostMapping
    public ResponseEntity<SemesterOfferingResponse> createOffering(
            @Valid @RequestBody SemesterOfferingRequest request) {

        SemesterOfferingResponse response = offeringService.createOffering(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 2. Get all semester offerings
    @GetMapping
    public ResponseEntity<List<SemesterOfferingResponse>> getAllOfferings() {
        return ResponseEntity.ok(offeringService.getAllOfferings());
    }

    // 2.1 Get semester offering by ID
    @GetMapping("/{offeringId}")
    public ResponseEntity<SemesterOfferingResponse> getOfferingById(
            @PathVariable Long offeringId) {

        return ResponseEntity.ok(
                offeringService.getOfferingById(offeringId)
        );
    }

    // 2.2 Update semester offering dates
    @PutMapping("/{offeringId}")
    public ResponseEntity<SemesterOfferingResponse> updateOffering(
            @PathVariable Long offeringId,
            @Valid @RequestBody SemesterOfferingUpdateRequest request) {

        return ResponseEntity.ok(
                offeringService.updateOffering(offeringId, request)
        );
    }

    // 3. Get semester offerings by academic year
    @GetMapping("/academic-year/{academicYearId}")
    public ResponseEntity<List<SemesterOfferingResponse>> getOfferingsByAcademicYear(
            @PathVariable Long academicYearId) {

        return ResponseEntity.ok(offeringService.getOfferingsByAcademicYear(academicYearId));
    }

    // 4. Get semester offerings by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<SemesterOfferingResponse>> getOfferingsByStatus(
            @PathVariable SemesterStatus status) {

        return ResponseEntity.ok(offeringService.getOfferingsByStatus(status));
    }

    // 5. Activate a semester offering (with Audit History)
    @PatchMapping("/{offeringId}/activate")
    public ResponseEntity<SemesterOfferingResponse> activateOffering(
            @PathVariable Long offeringId,
            @RequestParam(required = false, defaultValue = "Semester activated") String reason,
            Authentication authentication) {

        return ResponseEntity.ok(
                offeringService.activateOffering(
                        offeringId,
                        authentication.getName(),
                        reason
                )
        );
    }

    // 6. Complete a semester offering and close active enrollments
    @PatchMapping("/{offeringId}/complete")
    public ResponseEntity<SemesterOfferingResponse> completeOffering(
            @PathVariable Long offeringId) {

        return ResponseEntity.ok(offeringService.completeOffering(offeringId));
    }

    // 7. Cancel an upcoming semester offering
    @PatchMapping("/{offeringId}/cancel")
    public ResponseEntity<SemesterOfferingResponse> cancelOffering(
            @PathVariable Long offeringId) {

        return ResponseEntity.ok(
                offeringService.cancelOffering(offeringId)
        );
    }
}
