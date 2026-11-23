package com.campussync.repository;

import com.campussync.entity.AcademicYear;
import com.campussync.entity.Course;
import com.campussync.entity.CourseEnrollment;
import com.campussync.entity.Semester;
import com.campussync.entity.SemesterOffering;
import com.campussync.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseEnrollmentRepository
        extends JpaRepository<CourseEnrollment, Long> {

    Optional<CourseEnrollment> findByStudentAndActiveTrue(Student student);

    List<CourseEnrollment>
    findByCourseAndSemesterAndAcademicYearAndDivisionAndActiveTrue(
            Course course,
            Semester semester,
            AcademicYear academicYear,
            String division
    );

    // Check whether a course has active student enrollments
    boolean existsByCourseAndActiveTrue(Course course);

    // Find all enrollments belonging to an offering
    List<CourseEnrollment> findBySemesterOffering(
            SemesterOffering semesterOffering
    );

    // Find only active enrollments belonging to an offering
    List<CourseEnrollment> findBySemesterOfferingAndActiveTrue(
            SemesterOffering semesterOffering
    );

    // Check whether active enrollments exist
    boolean existsBySemesterOfferingAndActiveTrue(
            SemesterOffering semesterOffering
    );

    // Check for an identical active enrollment
    boolean existsByStudentAndCourseAndSemesterAndAcademicYearAndDivisionAndActiveTrue(
            Student student,
            Course course,
            Semester semester,
            AcademicYear academicYear,
            String division
    );

    // Check for duplicate active enrollment excluding current enrollment ID
    boolean existsByStudentAndCourseAndSemesterAndAcademicYearAndDivisionAndActiveTrueAndEnrollmentIdNot(
            Student student,
            Course course,
            Semester semester,
            AcademicYear academicYear,
            String division,
            Long enrollmentId
    );

    // Get all active enrollments
    List<CourseEnrollment> findByActiveTrue();

    // Find active enrollment by ID
    Optional<CourseEnrollment> findByEnrollmentIdAndActiveTrue(Long enrollmentId);
}
