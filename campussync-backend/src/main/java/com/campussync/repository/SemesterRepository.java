package com.campussync.repository;

import com.campussync.entity.Course;
import com.campussync.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {

    // Find all semesters belonging to a specific course
    List<Semester> findByCourse(Course course);

    // Find a semester by its number within a course
    Optional<Semester> findByCourseAndSemesterNumber(Course course, int semesterNumber);

    // Find only active semesters
    List<Semester> findByActiveTrue();

    // Find active semesters for a specific course
    List<Semester> findByCourseAndActiveTrue(Course course);

    // Check if a semester number already exists for a given course (useful for validation)
    boolean existsByCourseAndSemesterNumber(Course course, int semesterNumber);

}
