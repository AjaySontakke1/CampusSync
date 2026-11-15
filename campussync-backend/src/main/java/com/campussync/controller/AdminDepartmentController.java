package com.campussync.controller;

import com.campussync.dto.DepartmentRequest;
import com.campussync.dto.DepartmentResponse;
import com.campussync.service.DepartmentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/departments")
@RequiredArgsConstructor
public class AdminDepartmentController {

    private final DepartmentService departmentService;

    // 1. Create Department
    @PostMapping
    public ResponseEntity<DepartmentResponse> createDepartment(
            @Valid @RequestBody DepartmentRequest request) {

        DepartmentResponse response =
                departmentService.createDepartment(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 2. Get All Departments
    @GetMapping
    public ResponseEntity<List<DepartmentResponse>> getAllDepartments() {

        return ResponseEntity.ok(
                departmentService.getAllDepartments()
        );
    }

    // Filter Departments by Status
    @GetMapping("/filter")
    public ResponseEntity<List<DepartmentResponse>> getDepartmentsByStatus(
            @RequestParam Boolean active) {

        return ResponseEntity.ok(
                departmentService.getDepartmentsByStatus(active)
        );
    }

    // 3. Get Department By ID
    @GetMapping("/{id}")
    public ResponseEntity<DepartmentResponse> getDepartmentById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                departmentService.getDepartmentById(id)
        );
    }

    // 4. Update Department
    @PutMapping("/{id}")
    public ResponseEntity<DepartmentResponse> updateDepartment(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentRequest request) {

        return ResponseEntity.ok(
                departmentService.updateDepartment(id, request)
        );
    }

    // 5. Deactivate Department (Soft Delete)
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateDepartment(
            @PathVariable Long id) {

        departmentService.deactivateDepartment(id);

        return ResponseEntity.noContent().build();
    }

    // 6. Reactivate Department
    @PatchMapping("/{id}/reactivate")
    public ResponseEntity<Void> reactivateDepartment(
            @PathVariable Long id) {

        departmentService.reactivateDepartment(id);

        return ResponseEntity.noContent().build();
    }
}
