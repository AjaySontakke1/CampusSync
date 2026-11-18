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

    // 4. Update Course
    public CourseResponse updateCourse(Long id, CourseRequest request) {

        // 1. Find existing course
        Course course = courseRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Course not found with ID: " + id));

        // 2. Check whether another course uses this code
        courseRepository.findByCourseCode(request.getCourseCode())
                .ifPresent(existingCourse -> {
                    if (!existingCourse.getCourseId().equals(id)) {
                        throw new IllegalArgumentException(
                                "Course code already exists");
                    }
                });

        // 3. Find the new department
        Department department = departmentRepository.findById(
                request.getDepartmentId()
        ).orElseThrow(() ->
                new EntityNotFoundException(
                        "Department not found"));

        // 4. Check department status
        if (!Boolean.TRUE.equals(department.getActive())) {
            throw new IllegalArgumentException(
                    "Cannot assign course to inactive department");
        }

        // 5. Update course details
        course.setCourseName(request.getCourseName());
        course.setCourseCode(request.getCourseCode());
        course.setDurationYears(request.getDurationYears());
        course.setTotalSemesters(request.getTotalSemesters());
        course.setDepartment(department);

        // 6. Save updated course
        Course updatedCourse = courseRepository.save(course);

        // 7. Return response
        return mapToResponse(updatedCourse);
    }

    // 5. Deactivate Course
    public void deactivateCourse(Long id) {

        Course course = courseRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Course not found with ID: " + id));

        if (!course.isActive()) {
            throw new IllegalArgumentException(
                    "Course is already inactive");
        }

        course.setActive(false);

        courseRepository.save(course);
    }

    // 6. Reactivate Course
    public void reactivateCourse(Long id) {

        Course course = courseRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Course not found with ID: " + id));

        if (course.isActive()) {
            throw new IllegalArgumentException(
                    "Course is already active");
        }

        // Check whether its department is active
        if (!Boolean.TRUE.equals(course.getDepartment().getActive())) {
            throw new IllegalArgumentException(
                    "Cannot activate course under an inactive department");
        }

        course.setActive(true);

        courseRepository.save(course);
    }

    // 7. Convert Entity into Response DTO
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
