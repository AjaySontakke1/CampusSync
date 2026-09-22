package com.campussync.controller;

import com.campussync.dto.AssignmentProgressDTO;
import com.campussync.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/assignments")
    public ResponseEntity<List<AssignmentProgressDTO>> getMyAssignments(Authentication authentication) {
        String email = authentication.getName();
        List<AssignmentProgressDTO> assignments = studentService.getMyAssignments(email);
        return ResponseEntity.ok(assignments);
    }
}
