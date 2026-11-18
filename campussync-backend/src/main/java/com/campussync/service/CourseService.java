package com.campussync.service;

import com.campussync.dto.CourseRequest;
import com.campussync.dto.CourseResponse;
import com.campussync.entity.Course;
import com.campussync.entity.Department;
import com.campussync.repository.CourseRepository;
import com.campussync.repository.DepartmentRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;

    // 1. Create Course
    public CourseResponse createCourse(CourseRequest request) {

        // Check duplicate course code
        if (courseRepository.existsByCourseCode(
                request.getCourseCode())) {

            throw new IllegalArgumentException(
                    "Course code already exists");
        }

        // Find department
        Department department =
                departmentRepository.findById(
                        request.getDepartmentId())
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Department not found"));

        // Check department status
        if (!Boolean.TRUE.equals(department.getActive())) {
            throw new IllegalArgumentException(
                    "Cannot create course under inactive department");
        }

        // Build Course entity
        Course course = Course.builder()
                .courseName(request.getCourseName())
                .courseCode(request.getCourseCode())
                .durationYears(request.getDurationYears())
                .totalSemesters(request.getTotalSemesters())
                .active(true)
                .department(department)
                .build();

        // Save course
        Course savedCourse = courseRepository.save(course);

        // Return response DTO
        return mapToResponse(savedCourse);
    }

    // 2. Get All Courses
    @Transactional(readOnly = true)
    public List<CourseResponse> getAllCourses() {

        return courseRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // 3. Get Course By ID
    @Transactional(readOnly = true)
    public CourseResponse getCourseById(Long id) {

        Course course = courseRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Course not found with ID: " + id));

        return mapToResponse(course);
    }

    // 4. Convert Entity into Response DTO
    private CourseResponse mapToResponse(Course course) {

        return CourseResponse.builder()
                .courseId(course.getCourseId())
                .courseName(course.getCourseName())
                .courseCode(course.getCourseCode())
                .durationYears(course.getDurationYears())
                .totalSemesters(course.getTotalSemesters())
                .active(course.isActive())
                .departmentId(
                        course.getDepartment().getDepartmentId())
                .departmentName(
                        course.getDepartment().getDepartmentName())
                .build();
    }
}
