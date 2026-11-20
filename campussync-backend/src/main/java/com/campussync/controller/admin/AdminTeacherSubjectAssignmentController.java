package com.campussync.controller.admin;

import com.campussync.dto.request.TeacherSubjectAssignmentRequest;
import com.campussync.dto.response.TeacherSubjectAssignmentResponse;
import com.campussync.service.TeacherSubjectAssignmentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/teacher-subject-assignments")
@RequiredArgsConstructor
public class AdminTeacherSubjectAssignmentController {

    private final TeacherSubjectAssignmentService assignmentService;

    // CREATE ASSIGNMENT
    @PostMapping
    public ResponseEntity<TeacherSubjectAssignmentResponse> createAssignment(
            @Valid @RequestBody TeacherSubjectAssignmentRequest request) {

        TeacherSubjectAssignmentResponse response =
                assignmentService.createAssignment(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET ALL ASSIGNMENTS
    @GetMapping
    public ResponseEntity<List<TeacherSubjectAssignmentResponse>> getAllAssignments() {

        List<TeacherSubjectAssignmentResponse> assignments =
                assignmentService.getAllAssignments();

        return ResponseEntity.ok(assignments);
    }

    // GET ASSIGNMENT BY ID
    @GetMapping("/{id}")
    public ResponseEntity<TeacherSubjectAssignmentResponse> getAssignmentById(
            @PathVariable Long id) {

        TeacherSubjectAssignmentResponse response =
                assignmentService.getAssignmentById(id);

        return ResponseEntity.ok(response);
    }

    // GET ASSIGNMENTS BY TEACHER
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<TeacherSubjectAssignmentResponse>> getAssignmentsByTeacher(
            @PathVariable Long teacherId) {

        List<TeacherSubjectAssignmentResponse> assignments =
                assignmentService.getAssignmentsByTeacher(teacherId);

        return ResponseEntity.ok(assignments);
    }
}
