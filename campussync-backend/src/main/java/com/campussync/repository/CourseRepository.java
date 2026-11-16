package com.campussync.repository;

import com.campussync.entity.Course;
import com.campussync.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    // Check whether course code already exists
    boolean existsByCourseCode(String courseCode);

    // Find course using course code
    Optional<Course> findByCourseCode(String courseCode);

    // Get all courses belonging to a department
    List<Course> findByDepartment(Department department);

    // Get only active courses belonging to a department
    List<Course> findByDepartmentAndActiveTrue(Department department);

    // Check whether a department has active courses
    boolean existsByDepartmentAndActiveTrue(Department department);

    // Get all active courses
    List<Course> findByActiveTrue();
}
