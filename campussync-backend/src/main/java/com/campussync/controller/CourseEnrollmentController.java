package com.campussync.controller;

import com.campussync.dto.request.CourseEnrollmentRequest;
import com.campussync.dto.response.CourseEnrollmentResponse;
import com.campussync.service.CourseEnrollmentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/enrollments")
@RequiredArgsConstructor
public class CourseEnrollmentController {

    private final CourseEnrollmentService enrollmentService;

    // 1. Create Enrollment
    @PostMapping
    public ResponseEntity<CourseEnrollmentResponse> createEnrollment(
            @Valid @RequestBody CourseEnrollmentRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(enrollmentService.createEnrollment(request));
    }

    // 2. Get All Active Enrollments
    @GetMapping
    public ResponseEntity<List<CourseEnrollmentResponse>> getAllEnrollments() {

        return ResponseEntity.ok(
                enrollmentService.getAllEnrollments());
    }

    // 3. Get Enrollment By ID
    @GetMapping("/{enrollmentId}")
    public ResponseEntity<CourseEnrollmentResponse> getEnrollmentById(
            @PathVariable Long enrollmentId) {

        return ResponseEntity.ok(
                enrollmentService.getEnrollmentById(enrollmentId));
    }

    // 4. Filter Enrollments By Class
    @GetMapping("/class")
    public ResponseEntity<List<CourseEnrollmentResponse>> getClassEnrollments(
            @RequestParam Long courseId,
            @RequestParam Long semesterId,
            @RequestParam Long academicYearId,
            @RequestParam String division) {

        return ResponseEntity.ok(
                enrollmentService.getClassEnrollments(
                        courseId,
                        semesterId,
                        academicYearId,
                        division));
    }

    // 5. Update Enrollment
    @PutMapping("/{enrollmentId}")
    public ResponseEntity<CourseEnrollmentResponse> updateEnrollment(
            @PathVariable Long enrollmentId,
            @Valid @RequestBody CourseEnrollmentRequest request) {

        return ResponseEntity.ok(
                enrollmentService.updateEnrollment(enrollmentId, request));
    }

    // 6. Deactivate Enrollment
    @DeleteMapping("/{enrollmentId}")
    public ResponseEntity<Void> deactivateEnrollment(
            @PathVariable Long enrollmentId) {

        enrollmentService.deactivateEnrollment(enrollmentId);

        return ResponseEntity.noContent().build();
    }
}
