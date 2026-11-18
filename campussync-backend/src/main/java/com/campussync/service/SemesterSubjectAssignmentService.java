package com.campussync.service;

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

@Service
@RequiredArgsConstructor
public class SemesterSubjectAssignmentService {

    private final SemesterOfferingRepository offeringRepository;

    private final SemesterSubjectAssignmentRepository assignmentRepository;

    private final SubjectRepository subjectRepository;

    @Transactional
    public SemesterSubjectAssignment assignSubject(
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
        return assignmentRepository.save(assignment);
    }
}
