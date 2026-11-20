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

    // Find all subject assignments for an offering
    List<SemesterSubjectAssignment> findBySemesterOffering(
            SemesterOffering semesterOffering
    );

    // Find only active subject assignments
    List<SemesterSubjectAssignment> findBySemesterOfferingAndActiveTrue(
            SemesterOffering semesterOffering
    );

    // Find a particular assignment
    Optional<SemesterSubjectAssignment> findBySemesterOfferingAndSubject(
            SemesterOffering semesterOffering,
            Subject subject
    );

    // Find all assignments for a subject
    List<SemesterSubjectAssignment> findBySubject(Subject subject);
}
