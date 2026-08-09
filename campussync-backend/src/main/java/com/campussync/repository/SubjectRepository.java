package com.campussync.repository;

import com.campussync.entity.Semester;
import com.campussync.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    // Find all subjects in a specific semester
    List<Subject> findBySemester(Semester semester);

    // Find a subject by its unique code e.g. "CS101", "DBMS202"
    Optional<Subject> findBySubjectCode(String subjectCode);

    // Find only active subjects
    List<Subject> findByActiveTrue();

    // Find active subjects for a specific semester
    List<Subject> findBySemesterAndActiveTrue(Semester semester);

    // Check if a subject code already exists (useful for validation)
    boolean existsBySubjectCode(String subjectCode);

}
