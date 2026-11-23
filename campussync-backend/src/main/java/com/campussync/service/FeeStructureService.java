package com.campussync.service;

import com.campussync.dto.FeeStructureRequestDTO;
import com.campussync.dto.FeeStructureResponseDTO;
import com.campussync.entity.*;
import com.campussync.repository.*;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeeStructureService {

    private final FeeStructureRepository feeStructureRepository;
    private final CourseRepository courseRepository;
    private final SemesterRepository semesterRepository;
    private final AcademicYearRepository academicYearRepository;

    @Transactional
    public FeeStructureResponseDTO createFeeStructure(
            FeeStructureRequestDTO request) {

        // 1. Find Course
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Course not found"));

        // 2. Check Course is active
        if (!course.isActive()) {
            throw new IllegalArgumentException(
                    "Cannot create fee structure for inactive course");
        }

        // 3. Find Academic Year
        AcademicYear academicYear =
                academicYearRepository.findById(request.getAcademicYearId())
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Academic year not found"));

        // 4. Find Semester if provided
        Semester semester = null;

        if (request.getSemesterId() != null) {

            semester = semesterRepository.findById(
                    request.getSemesterId()
            ).orElseThrow(() ->
                    new EntityNotFoundException("Semester not found"));

            // 5. Validate Semester belongs to Course
            if (!semester.getCourse().getCourseId()
                    .equals(course.getCourseId())) {

                throw new IllegalArgumentException(
                        "Semester does not belong to selected course");
            }

            if (!semester.isActive()) {
                throw new IllegalArgumentException(
                        "Cannot use inactive semester");
            }
        }

        // 6. Validate due date
        if (request.getDueDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Due date cannot be in the past");
        }

        // 7. Check duplicate structure
        boolean exists;

        if (semester == null) {

            exists = feeStructureRepository
                    .existsByCourseAndSemesterIsNullAndAcademicYearAndActiveTrue(
                            course, academicYear);

        } else {

            exists = feeStructureRepository
                    .existsByCourseAndSemesterAndAcademicYearAndActiveTrue(
                            course, semester, academicYear);
        }

        if (exists) {
            throw new IllegalArgumentException(
                    "Fee structure already exists for these details");
        }

        // 8. Create FeeStructure
        FeeStructure feeStructure = FeeStructure.builder()
                .course(course)
                .semester(semester)
                .academicYear(academicYear)
                .amount(request.getAmount())
                .dueDate(request.getDueDate())
                .active(true)
                .build();

        // 9. Save
        FeeStructure saved =
                feeStructureRepository.save(feeStructure);

        // 10. Return response
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<FeeStructureResponseDTO> getAllFeeStructures() {

        return feeStructureRepository.findByActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public FeeStructureResponseDTO getFeeStructureById(
            Long feeStructureId) {

        FeeStructure feeStructure =
                feeStructureRepository
                        .findByFeeStructureIdAndActiveTrue(feeStructureId)
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Active fee structure not found: "
                                                + feeStructureId));

        return mapToResponse(feeStructure);
    }

    @Transactional
    public FeeStructureResponseDTO updateFeeStructure(
            Long feeStructureId,
            FeeStructureRequestDTO request) {

        // 1. Find existing fee structure
        FeeStructure feeStructure =
                feeStructureRepository.findById(feeStructureId)
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Fee structure not found"));

        // 2. Prevent updating inactive structure
        if (!feeStructure.isActive()) {
            throw new IllegalArgumentException(
                    "Inactive fee structure cannot be updated");
        }

        // 3. Find Course
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Course not found"));

        if (!course.isActive()) {
            throw new IllegalArgumentException("Course is inactive");
        }

        // 4. Find Academic Year
        AcademicYear academicYear =
                academicYearRepository.findById(request.getAcademicYearId())
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Academic year not found"));

        // 5. Find Semester if provided
        Semester semester = null;

        if (request.getSemesterId() != null) {

            semester = semesterRepository.findById(request.getSemesterId())
                    .orElseThrow(() ->
                            new EntityNotFoundException("Semester not found"));

            if (!semester.isActive()) {
                throw new IllegalArgumentException("Semester is inactive");
            }

            if (!semester.getCourse().getCourseId()
                    .equals(course.getCourseId())) {

                throw new IllegalArgumentException(
                        "Semester does not belong to selected course");
            }
        }

        // 6. Validate due date
        if (request.getDueDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Due date cannot be in the past");
        }

        // 7. Check duplicate structure, excluding current record
        boolean duplicate;

        if (semester == null) {

            duplicate = feeStructureRepository
                    .existsByCourseAndSemesterIsNullAndAcademicYearAndActiveTrueAndFeeStructureIdNot(
                            course, academicYear, feeStructureId);

        } else {

            duplicate = feeStructureRepository
                    .existsByCourseAndSemesterAndAcademicYearAndActiveTrueAndFeeStructureIdNot(
                            course, semester, academicYear, feeStructureId);
        }

        if (duplicate) {
            throw new IllegalArgumentException(
                    "Another active fee structure already exists");
        }

        // 8. Update existing structure
        feeStructure.setCourse(course);
        feeStructure.setSemester(semester);
        feeStructure.setAcademicYear(academicYear);
        feeStructure.setAmount(request.getAmount());
        feeStructure.setDueDate(request.getDueDate());

        // 9. Save
        FeeStructure updated =
                feeStructureRepository.save(feeStructure);

        return mapToResponse(updated);
    }

    private FeeStructureResponseDTO mapToResponse(
            FeeStructure feeStructure) {

        return FeeStructureResponseDTO.builder()
                .feeStructureId(feeStructure.getFeeStructureId())

                .courseId(feeStructure.getCourse().getCourseId())
                .courseName(feeStructure.getCourse().getCourseName())

                .semesterId(
                        feeStructure.getSemester() != null
                                ? feeStructure.getSemester().getSemesterId()
                                : null)

                .semesterName(
                        feeStructure.getSemester() != null
                                ? feeStructure.getSemester().getSemesterName()
                                : "All Semesters")

                .academicYearId(
                        feeStructure.getAcademicYear().getAcademicYearId())

                .academicYearName(
                        feeStructure.getAcademicYear().getName())

                .amount(feeStructure.getAmount())
                .dueDate(feeStructure.getDueDate())
                .active(feeStructure.isActive())
                .build();
    }
}
