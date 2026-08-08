package com.campussync.repository;

import com.campussync.entity.Course;
import com.campussync.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    // Find a course by its unique code e.g. "BSCS", "MSCS"
    Optional<Course> findByCourseCode(String courseCode);

    // Find all courses under a specific department
    List<Course> findByDepartment(Department department);

    // Find only active courses
    List<Course> findByActiveTrue();

    // Find active courses under a specific department
    List<Course> findByDepartmentAndActiveTrue(Department department);

    // Check if a course code already exists (useful for validation)
    boolean existsByCourseCode(String courseCode);

}
