package com.campussync.controller;

import com.campussync.dto.CourseRequest;
import com.campussync.dto.CourseResponse;
import com.campussync.service.CourseService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/courses")
@RequiredArgsConstructor
public class AdminCourseController {

    private final CourseService courseService;

    // 1. Create Course
    @PostMapping
    public ResponseEntity<CourseResponse> createCourse(
            @Valid @RequestBody CourseRequest request) {

        CourseResponse response = courseService.createCourse(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 2. Get All Courses
    @GetMapping
    public ResponseEntity<List<CourseResponse>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    // 3. Get Course by ID
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourseById(
            @PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    // 4. Update Course
    @PutMapping("/{id}")
    public ResponseEntity<CourseResponse> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseRequest request) {

        return ResponseEntity.ok(
                courseService.updateCourse(id, request)
        );
    }

    // 5. Deactivate Course
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateCourse(
            @PathVariable Long id) {

        courseService.deactivateCourse(id);

        return ResponseEntity.noContent().build();
    }

    // 6. Reactivate Course
    @PatchMapping("/{id}/reactivate")
    public ResponseEntity<Void> reactivateCourse(
            @PathVariable Long id) {

        courseService.reactivateCourse(id);

        return ResponseEntity.noContent().build();
    }
}
