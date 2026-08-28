package com.campussync.controller;

import com.campussync.entity.Student;
import com.campussync.service.ParentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/parent")
public class ParentController {

    private final ParentService parentService;

    public ParentController(ParentService parentService) {
        this.parentService = parentService;
    }

    @GetMapping("/students")
    public ResponseEntity<List<Student>> getMyStudents(Authentication authentication) {
        String email = authentication.getName();
        List<Student> students = parentService.getMyStudents(email);
        return ResponseEntity.ok(students);
    }
}
