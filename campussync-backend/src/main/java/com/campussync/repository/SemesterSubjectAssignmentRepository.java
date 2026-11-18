package com.campussync.repository;

import com.campussync.entity.SemesterOffering;
import com.campussync.entity.SemesterSubjectAssignment;
import com.campussync.entity.Subject;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SemesterSubjectAssignmentRepository
        extends JpaRepository<SemesterSubjectAssignment, Long> {

    // Check duplicate subject assignment
    boolean existsBySemesterOfferingAndSubject(
            SemesterOffering semesterOffering,
            Subject subject
    );

    // Find all assignments for an offering
    List<SemesterSubjectAssignment>
    findBySemesterOffering(SemesterOffering semesterOffering);

    // Find active assignments for an offering
    List<SemesterSubjectAssignment>
    findBySemesterOfferingAndActiveTrue(
            SemesterOffering semesterOffering
    );

    // Find assignments for a particular subject
    List<SemesterSubjectAssignment> findBySubject(Subject subject);

    // Find a particular assignment
    Optional<SemesterSubjectAssignment>
    findBySemesterOfferingAndSubject(
            SemesterOffering semesterOffering,
            Subject subject
    );
}
