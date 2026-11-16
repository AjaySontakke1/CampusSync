package com.campussync.controller;

import com.campussync.dto.DepartmentRequest;
import com.campussync.dto.DepartmentResponse;
import com.campussync.dto.DepartmentStatusHistoryResponse;
import com.campussync.service.DepartmentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

    // 2. Combined Search, Filter and Pagination
    @GetMapping
    public ResponseEntity<Page<DepartmentResponse>> searchDepartments(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                departmentService.searchDepartments(
                        keyword, active, page, size)
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

    // Paginated Departments
    @GetMapping("/page")
    public ResponseEntity<Page<DepartmentResponse>> getDepartmentsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                departmentService.getAllDepartments(page, size)
        );
    }

    // Search Departments by Name or Code
    @GetMapping("/search")
    public ResponseEntity<List<DepartmentResponse>> searchDepartments(
            @RequestParam String keyword) {

        return ResponseEntity.ok(
                departmentService.searchDepartments(keyword)
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

    // 5. Deactivate Department (Soft Delete with Audit Log)
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateDepartment(
            @PathVariable Long id,
            @RequestParam String reason,
            Authentication authentication) {

        departmentService.deactivateDepartment(
                id,
                authentication.getName(),
                reason
        );

        return ResponseEntity.noContent().build();
    }

    // 6. Reactivate Department (with Audit Log)
    @PatchMapping("/{id}/reactivate")
    public ResponseEntity<Void> reactivateDepartment(
            @PathVariable Long id,
            @RequestParam String reason,
            Authentication authentication) {

        departmentService.reactivateDepartment(
                id,
                authentication.getName(),
                reason
        );

        return ResponseEntity.noContent().build();
    }

    // 7. Get Department Status Change History (Audit Log)
    @GetMapping("/{id}/history")
    public ResponseEntity<List<DepartmentStatusHistoryResponse>> getDepartmentHistory(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                departmentService.getDepartmentHistory(id)
        );
    }
}
