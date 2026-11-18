package com.campussync.service;

import com.campussync.dto.response.SemesterSubjectAssignmentResponse;
import com.campussync.entity.SemesterOffering;
import com.campussync.entity.SemesterSubjectAssignment;
import com.campussync.entity.Subject;

import com.campussync.enums.SemesterStatus;

import com.campussync.repository.SemesterOfferingRepository;
import com.campussync.repository.SemesterSubjectAssignmentRepository;
import com.campussync.repository.SubjectRepository;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SemesterSubjectAssignmentService {

    private final SemesterOfferingRepository offeringRepository;

    private final SemesterSubjectAssignmentRepository assignmentRepository;

    private final SubjectRepository subjectRepository;

    @Transactional
    public SemesterSubjectAssignmentResponse assignSubject(
            Long offeringId,
            Long subjectId) {

        // 1. Find semester offering
        SemesterOffering offering = offeringRepository.findById(offeringId)
                .orElseThrow(() ->
                    new EntityNotFoundException(
                        "Semester offering not found"));

        // 2. Check offering status
        if (offering.getStatus() != SemesterStatus.ACTIVE) {
            throw new IllegalArgumentException(
                "Subjects can only be assigned to active offerings");
        }

        // 3. Find subject
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() ->
                    new EntityNotFoundException(
                        "Subject not found"));

        // 4. Check duplicate assignment
        if (assignmentRepository
                .existsBySemesterOfferingAndSubject(offering, subject)) {

            throw new IllegalArgumentException(
                "Subject is already assigned to this offering");
        }

        // 5. Create assignment
        SemesterSubjectAssignment assignment =
                SemesterSubjectAssignment.builder()
                        .semesterOffering(offering)
                        .subject(subject)
                        .active(true)
                        .build();

        // 6. Save assignment
        SemesterSubjectAssignment savedAssignment =
                assignmentRepository.save(assignment);

        return mapToResponse(savedAssignment);
    }

    // Get all subject assignments for an offering
    @Transactional(readOnly = true)
    public List<SemesterSubjectAssignmentResponse> getAssignmentsByOffering(
            Long offeringId) {

        SemesterOffering offering = offeringRepository.findById(offeringId)
                .orElseThrow(() ->
                    new EntityNotFoundException(
                        "Semester offering not found"));

        return assignmentRepository.findBySemesterOffering(offering)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Get only active subject assignments
    @Transactional(readOnly = true)
    public List<SemesterSubjectAssignmentResponse> getActiveAssignments(
            Long offeringId) {

        SemesterOffering offering = offeringRepository.findById(offeringId)
                .orElseThrow(() ->
                    new EntityNotFoundException(
                        "Semester offering not found"));

        return assignmentRepository
                .findBySemesterOfferingAndActiveTrue(offering)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Deactivate a subject assignment
    @Transactional
    public void deactivateAssignment(Long assignmentId) {

        SemesterSubjectAssignment assignment =
                assignmentRepository.findById(assignmentId)
                        .orElseThrow(() ->
                            new EntityNotFoundException(
                                "Subject assignment not found"));

        if (!assignment.isActive()) {
            throw new IllegalArgumentException(
                "Subject assignment is already inactive");
        }

        assignment.setActive(false);

        assignmentRepository.save(assignment);
    }

    private SemesterSubjectAssignmentResponse mapToResponse(
            SemesterSubjectAssignment assignment) {

        return SemesterSubjectAssignmentResponse.builder()
                .assignmentId(assignment.getAssignmentId())
                .semesterOfferingId(
                    assignment.getSemesterOffering().getSemesterOfferingId())
                .subjectId(assignment.getSubject().getSubjectId())
                .subjectName(assignment.getSubject().getSubjectName())
                .subjectCode(assignment.getSubject().getSubjectCode())
                .active(assignment.isActive())
                .build();
    }
}
