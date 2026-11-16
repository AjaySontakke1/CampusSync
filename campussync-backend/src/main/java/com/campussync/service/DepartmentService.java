package com.campussync.service;

import com.campussync.dto.DepartmentRequest;
import com.campussync.dto.DepartmentResponse;
import com.campussync.dto.DepartmentStatusHistoryResponse;
import com.campussync.entity.Department;
import com.campussync.entity.DepartmentStatusHistory;
import com.campussync.entity.User;
import com.campussync.repository.CourseRepository;
import com.campussync.repository.DepartmentRepository;
import com.campussync.repository.DepartmentStatusHistoryRepository;
import com.campussync.repository.TeacherRepository;
import com.campussync.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;
    private final DepartmentStatusHistoryRepository departmentStatusHistoryRepository;

    // 1. Create Department
    public DepartmentResponse createDepartment(DepartmentRequest request) {

        if (departmentRepository.existsByDepartmentName(
                request.getDepartmentName())) {
            throw new IllegalArgumentException(
                    "Department name already exists");
        }

        if (departmentRepository.existsByDepartmentCode(
                request.getDepartmentCode())) {
            throw new IllegalArgumentException(
                    "Department code already exists");
        }

        Department department = Department.builder()
                .departmentName(request.getDepartmentName())
                .departmentCode(request.getDepartmentCode())
                .hodName(request.getHodName())
                .build();

        Department savedDepartment =
                departmentRepository.save(department);

        return mapToResponse(savedDepartment);
    }

    // 2. Get All Departments
    @Transactional(readOnly = true)
    public List<DepartmentResponse> getAllDepartments() {

        return departmentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Paginated Departments
    @Transactional(readOnly = true)
    public Page<DepartmentResponse> getAllDepartments(
            int page, int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("departmentName").ascending()
        );

        return departmentRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    // Filter Departments by Status
    @Transactional(readOnly = true)
    public List<DepartmentResponse> getDepartmentsByStatus(
            Boolean active) {

        return departmentRepository.findByActive(active)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Search Departments by Name or Code
    @Transactional(readOnly = true)
    public List<DepartmentResponse> searchDepartments(String keyword) {

        return departmentRepository
                .findByDepartmentNameContainingIgnoreCaseOrDepartmentCodeContainingIgnoreCase(
                        keyword, keyword)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Combined Search, Status Filter and Pagination
    @Transactional(readOnly = true)
    public Page<DepartmentResponse> searchDepartments(
            String keyword,
            Boolean active,
            int page,
            int size) {

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page number cannot be negative");
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Page size must be between 1 and 100");
        }

        if (keyword != null && keyword.isBlank()) {
            keyword = null;
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("departmentName").ascending()
        );

        return departmentRepository
                .searchDepartments(keyword, active, pageable)
                .map(this::mapToResponse);
    }

    // 3. Get Department By ID
    @Transactional(readOnly = true)
    public DepartmentResponse getDepartmentById(Long id) {

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Department not found with ID: " + id));

        return mapToResponse(department);
    }

    // 4. Update Department
    public DepartmentResponse updateDepartment(
            Long id, DepartmentRequest request) {

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Department not found with ID: " + id));

        // Check duplicate department name
        departmentRepository.findAll().stream()
                .filter(d -> d.getDepartmentName()
                        .equalsIgnoreCase(request.getDepartmentName()))
                .filter(d -> !d.getDepartmentId().equals(id))
                .findFirst()
                .ifPresent(d -> {
                    throw new IllegalArgumentException(
                            "Department name already exists");
                });

        // Check duplicate department code
        departmentRepository.findAll().stream()
                .filter(d -> d.getDepartmentCode()
                        .equalsIgnoreCase(request.getDepartmentCode()))
                .filter(d -> !d.getDepartmentId().equals(id))
                .findFirst()
                .ifPresent(d -> {
                    throw new IllegalArgumentException(
                            "Department code already exists");
                });

        department.setDepartmentName(request.getDepartmentName());
        department.setDepartmentCode(request.getDepartmentCode());
        department.setHodName(request.getHodName());

        Department updatedDepartment =
                departmentRepository.save(department);

        return mapToResponse(updatedDepartment);
    }

    // 5. Deactivate Department (Soft Delete with Referential Integrity Check & Audit Logging)
    @Transactional
    public void deactivateDepartment(
            Long id,
            String adminEmail,
            String reason) {

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Department not found with ID: " + id));

        if (Boolean.FALSE.equals(department.getActive())) {
            throw new IllegalArgumentException(
                    "Department is already inactive");
        }

        // Check active courses
        if (courseRepository.existsByDepartmentAndActiveTrue(department)) {
            throw new IllegalArgumentException(
                    "Cannot deactivate department with active courses");
        }

        // Check assigned teachers
        if (teacherRepository.existsByDepartment(department)) {
            throw new IllegalArgumentException(
                    "Cannot deactivate department with assigned teachers");
        }

        // Find Admin
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Admin user not found"));

        // Save department status history
        DepartmentStatusHistory history =
                DepartmentStatusHistory.builder()
                        .department(department)
                        .previousStatus(department.getActive())
                        .newStatus(false)
                        .changedBy(admin)
                        .reason(reason)
                        .build();

        departmentStatusHistoryRepository.save(history);

        // Update department status
        department.setActive(false);

        departmentRepository.save(department);
    }

    // 6. Reactivate Department (with Audit Logging)
    @Transactional
    public void reactivateDepartment(
            Long id,
            String adminEmail,
            String reason) {

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Department not found with ID: " + id));

        if (Boolean.TRUE.equals(department.getActive())) {
            throw new IllegalArgumentException(
                    "Department is already active");
        }

        // Find Admin
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Admin user not found"));

        // Save department status history
        DepartmentStatusHistory history =
                DepartmentStatusHistory.builder()
                        .department(department)
                        .previousStatus(department.getActive())
                        .newStatus(true)
                        .changedBy(admin)
                        .reason(reason != null && !reason.isBlank() ? reason : "Reactivated by admin")
                        .build();

        departmentStatusHistoryRepository.save(history);

        // Update department status
        department.setActive(true);

        departmentRepository.save(department);
    }

    // 7. Get Department Status Change History (Audit Log)
    @Transactional(readOnly = true)
    public List<DepartmentStatusHistoryResponse> getDepartmentHistory(Long departmentId) {

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Department not found with ID: " + departmentId));

        return departmentStatusHistoryRepository
                .findByDepartmentOrderByChangedAtDesc(department)
                .stream()
                .map(this::mapToHistoryResponse)
                .toList();
    }

    private DepartmentStatusHistoryResponse mapToHistoryResponse(DepartmentStatusHistory history) {
        String adminName = null;
        if (history.getChangedBy() != null) {
            adminName = (history.getChangedBy().getFirstName() + " " + history.getChangedBy().getLastName()).trim();
        }

        return DepartmentStatusHistoryResponse.builder()
                .historyId(history.getHistoryId())
                .departmentId(history.getDepartment() != null ? history.getDepartment().getDepartmentId() : null)
                .departmentName(history.getDepartment() != null ? history.getDepartment().getDepartmentName() : null)
                .previousStatus(history.getPreviousStatus())
                .newStatus(history.getNewStatus())
                .changedByUserId(history.getChangedBy() != null ? history.getChangedBy().getId() : null)
                .changedByEmail(history.getChangedBy() != null ? history.getChangedBy().getEmail() : null)
                .changedByName(adminName)
                .reason(history.getReason())
                .changedAt(history.getChangedAt())
                .build();
    }

    // Convert Entity into Response DTO
    private DepartmentResponse mapToResponse(Department department) {

        return DepartmentResponse.builder()
                .departmentId(department.getDepartmentId())
                .departmentName(department.getDepartmentName())
                .departmentCode(department.getDepartmentCode())
                .hodName(department.getHodName())
                .active(department.getActive())
                .build();
    }
}
