package com.campussync.service;

import com.campussync.dto.request.SemesterOfferingRequest;
import com.campussync.dto.response.SemesterOfferingResponse;

import com.campussync.entity.CourseEnrollment;
import com.campussync.entity.Semester;
import com.campussync.entity.AcademicYear;
import com.campussync.entity.SemesterOffering;

import com.campussync.enums.SemesterStatus;

import com.campussync.repository.CourseEnrollmentRepository;
import com.campussync.repository.SemesterRepository;
import com.campussync.repository.AcademicYearRepository;
import com.campussync.repository.SemesterOfferingRepository;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SemesterOfferingService {

    private final SemesterRepository semesterRepository;

    private final AcademicYearRepository academicYearRepository;

    private final SemesterOfferingRepository offeringRepository;

    private final CourseEnrollmentRepository courseEnrollmentRepository;

    @Transactional
    public SemesterOfferingResponse createOffering(
            SemesterOfferingRequest request) {

        // 1. Find semester
        Semester semester = semesterRepository
                .findById(request.getSemesterId())
                .orElseThrow(() ->
                    new EntityNotFoundException("Semester not found"));

        // 2. Find academic year
        AcademicYear academicYear = academicYearRepository
                .findById(request.getAcademicYearId())
                .orElseThrow(() ->
                    new EntityNotFoundException("Academic year not found"));

        // 3. Validate dates
        if (!request.getStartDate().isBefore(request.getEndDate())) {
            throw new IllegalArgumentException(
                    "Start date must be before end date");
        }

        // 4. Check academic year date range
        if (request.getStartDate().isBefore(academicYear.getStartDate())
                || request.getEndDate().isAfter(academicYear.getEndDate())) {

            throw new IllegalArgumentException(
                    "Offering dates must be within the academic year");
        }

        // 5. Check duplicate offering
        if (offeringRepository.existsBySemesterAndAcademicYear(
                semester, academicYear)) {

            throw new IllegalArgumentException(
                    "Offering already exists for this semester and academic year");
        }

        // 6. Create offering
        SemesterOffering offering = SemesterOffering.builder()
                .semester(semester)
                .academicYear(academicYear)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(SemesterStatus.UPCOMING)
                .build();

        // 7. Save offering
        SemesterOffering savedOffering =
                offeringRepository.save(offering);

        // 8. Convert entity into response DTO
        return mapToResponse(savedOffering);
    }

    @Transactional(readOnly = true)
    public List<SemesterOfferingResponse> getAllOfferings() {

        return offeringRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SemesterOfferingResponse getOfferingById(Long offeringId) {

        SemesterOffering offering = offeringRepository
                .findById(offeringId)
                .orElseThrow(() ->
                    new EntityNotFoundException(
                        "Semester offering not found with ID: " + offeringId
                    ));

        return mapToResponse(offering);
    }

    @Transactional(readOnly = true)
    public List<SemesterOfferingResponse> getOfferingsByAcademicYear(
            Long academicYearId) {

        AcademicYear academicYear = academicYearRepository
                .findById(academicYearId)
                .orElseThrow(() ->
                    new EntityNotFoundException("Academic year not found"));

        return offeringRepository.findByAcademicYear(academicYear)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SemesterOfferingResponse> getOfferingsByStatus(
            SemesterStatus status) {

        return offeringRepository.findByStatus(status)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public SemesterOfferingResponse activateOffering(Long offeringId) {

        // 1. Find offering
        SemesterOffering offering = offeringRepository.findById(offeringId)
                .orElseThrow(() ->
                    new EntityNotFoundException(
                        "Semester offering not found"));

        // 2. Check current status
        if (offering.getStatus() != SemesterStatus.UPCOMING) {
            throw new IllegalArgumentException(
                "Only upcoming offerings can be activated");
        }

        // 3. Check semester dates
        LocalDate today = LocalDate.now();

        if (today.isBefore(offering.getStartDate())) {
            throw new IllegalArgumentException(
                "Semester cannot be activated before its start date");
        }

        if (today.isAfter(offering.getEndDate())) {
            throw new IllegalArgumentException(
                "Semester end date has already passed");
        }

        // 4. Activate offering
        offering.setStatus(SemesterStatus.ACTIVE);

        // 5. Return response DTO
        return mapToResponse(offering);
    }

    @Transactional
    public SemesterOfferingResponse completeOffering(Long offeringId) {

        // 1. Find semester offering
        SemesterOffering offering = offeringRepository.findById(offeringId)
                .orElseThrow(() ->
                    new EntityNotFoundException(
                        "Semester offering not found"));

        // 2. Check current status
        if (offering.getStatus() != SemesterStatus.ACTIVE) {
            throw new IllegalArgumentException(
                "Only active semester offerings can be completed");
        }

        // 3. Find all active student enrollments
        List<CourseEnrollment> enrollments =
                courseEnrollmentRepository
                    .findBySemesterOfferingAndActiveTrue(offering);

        // 4. Deactivate all enrollments
        for (CourseEnrollment enrollment : enrollments) {
            enrollment.setActive(false);
        }

        // 5. Complete the semester offering
        offering.setStatus(SemesterStatus.COMPLETED);

        // 6. Return response
        return mapToResponse(offering);
    }

    // Convert entity into response DTO
    private SemesterOfferingResponse mapToResponse(
            SemesterOffering offering) {

        return SemesterOfferingResponse.builder()
                .semesterOfferingId(offering.getSemesterOfferingId())

                .semesterId(offering.getSemester().getSemesterId())
                .semesterName(offering.getSemester().getSemesterName())
                .semesterNumber(offering.getSemester().getSemesterNumber())

                .academicYearId(
                    offering.getAcademicYear().getAcademicYearId())
                .academicYearName(offering.getAcademicYear().getName())

                .startDate(offering.getStartDate())
                .endDate(offering.getEndDate())

                .status(offering.getStatus())
                .build();
    }
}
