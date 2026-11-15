package com.campussync.service;

import com.campussync.dto.DepartmentRequest;
import com.campussync.dto.DepartmentResponse;
import com.campussync.entity.Department;
import com.campussync.repository.DepartmentRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

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

    // 5. Deactivate Department (Soft Delete)
    public void deactivateDepartment(Long id) {

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Department not found with ID: " + id));

        if (Boolean.FALSE.equals(department.getActive())) {
            throw new IllegalArgumentException(
                    "Department is already inactive");
        }

        department.setActive(false);

        departmentRepository.save(department);
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
