package com.campussync.repository;

import com.campussync.entity.FeeStructure;
import com.campussync.entity.Course;
import com.campussync.entity.Semester;
import com.campussync.entity.AcademicYear;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeeStructureRepository
        extends JpaRepository<FeeStructure, Long> {

    // Get all active fee structures
    List<FeeStructure> findByActiveTrue();

    // Find fee structures for a course and academic year
    List<FeeStructure> findByCourseAndAcademicYearAndActiveTrue(
            Course course,
            AcademicYear academicYear
    );

    // Find a semester-specific fee structure
    Optional<FeeStructure> findByCourseAndSemesterAndAcademicYearAndActiveTrue(
            Course course,
            Semester semester,
            AcademicYear academicYear
    );

    // Check duplicate course-wide fee structure
    boolean existsByCourseAndSemesterIsNullAndAcademicYearAndActiveTrue(
            Course course,
            AcademicYear academicYear
    );

    // Check duplicate semester-specific fee structure
    boolean existsByCourseAndSemesterAndAcademicYearAndActiveTrue(
            Course course,
            Semester semester,
            AcademicYear academicYear
    );
}
