package com.campussync.service;

import com.campussync.dto.request.TeacherSubjectAssignmentRequest;
import com.campussync.dto.response.TeacherSubjectAssignmentResponse;
import com.campussync.entity.*;
import com.campussync.repository.*;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TeacherSubjectAssignmentService {

    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;
    private final CourseRepository courseRepository;
    private final SemesterRepository semesterRepository;
    private final AcademicYearRepository academicYearRepository;
    private final TeacherSubjectAssignmentRepository assignmentRepository;

    // CREATE ASSIGNMENT
    public TeacherSubjectAssignmentResponse createAssignment(
            TeacherSubjectAssignmentRequest request) {

        // 1. Find Teacher
        Teacher teacher = teacherRepository.findById(request.getTeacherId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Teacher not found"));

        // 2. Check Teacher is active
        if (!Boolean.TRUE.equals(teacher.getUser().getActive())) {
            throw new IllegalArgumentException(
                    "Cannot assign an inactive teacher");
        }

        // 3. Find Subject
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Subject not found"));

        if (!subject.isActive()) {
            throw new IllegalArgumentException("Subject is inactive");
        }

        // 4. Find Course
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Course not found"));

        if (!course.isActive()) {
            throw new IllegalArgumentException("Course is inactive");
        }

        // 5. Find Semester
        Semester semester = semesterRepository.findById(request.getSemesterId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Semester not found"));

        if (!semester.isActive()) {
            throw new IllegalArgumentException("Semester is inactive");
        }

        // 6. Validate Semester belongs to Course
        if (!semester.getCourse().getCourseId().equals(course.getCourseId())) {
            throw new IllegalArgumentException(
                    "Selected semester does not belong to this course");
        }

        // 7. Validate Subject belongs to Semester
        if (!subject.getSemester().getSemesterId()
                .equals(semester.getSemesterId())) {
            throw new IllegalArgumentException(
                    "Selected subject does not belong to this semester");
        }

        // 8. Find Academic Year
        AcademicYear academicYear =
                academicYearRepository.findById(request.getAcademicYearId())
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Academic year not found"));

        if (!academicYear.isActive()) {
            throw new IllegalArgumentException(
                    "Academic year is inactive");
        }

        // 9. Normalize division
        String division = request.getDivision().trim().toUpperCase();

        // 10. Check duplicate assignment
        boolean alreadyAssigned =
                assignmentRepository
                        .existsBySubjectAndCourseAndSemesterAndAcademicYearAndDivision(
                                subject, course, semester, academicYear, division);

        if (alreadyAssigned) {
            throw new IllegalArgumentException(
                    "A teacher is already assigned to this subject and division");
        }

        // 11. Create assignment
        TeacherSubjectAssignment assignment =
                TeacherSubjectAssignment.builder()
                        .teacher(teacher)
                        .subject(subject)
                        .course(course)
                        .semester(semester)
                        .academicYear(academicYear)
                        .division(division)
                        .build();

        // 12. Save assignment
        TeacherSubjectAssignment saved =
                assignmentRepository.save(assignment);

        // 13. Return response DTO
        return mapToResponse(saved);
    }

    // GET ALL ASSIGNMENTS
    @Transactional(readOnly = true)
    public List<TeacherSubjectAssignmentResponse> getAllAssignments() {

        return assignmentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // GET ASSIGNMENT BY ID
    @Transactional(readOnly = true)
    public TeacherSubjectAssignmentResponse getAssignmentById(Long id) {

        TeacherSubjectAssignment assignment =
                assignmentRepository.findById(id)
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Assignment not found"));

        return mapToResponse(assignment);
    }

    // GET ASSIGNMENTS BY TEACHER
    @Transactional(readOnly = true)
    public List<TeacherSubjectAssignmentResponse> getAssignmentsByTeacher(
            Long teacherId) {

        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Teacher not found"));

        return assignmentRepository.findByTeacher(teacher)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // MAP ENTITY TO RESPONSE DTO
    private TeacherSubjectAssignmentResponse mapToResponse(
            TeacherSubjectAssignment assignment) {

        Teacher teacher = assignment.getTeacher();
        User user = teacher.getUser();
        Subject subject = assignment.getSubject();
        Course course = assignment.getCourse();
        Semester semester = assignment.getSemester();
        AcademicYear academicYear = assignment.getAcademicYear();

        return TeacherSubjectAssignmentResponse.builder()
                .assignmentId(assignment.getAssignmentId())

                .teacherId(teacher.getTeacherId())
                .teacherName(user.getFirstName() + " " + user.getLastName())

                .subjectId(subject.getSubjectId())
                .subjectName(subject.getSubjectName())
                .subjectCode(subject.getSubjectCode())

                .courseId(course.getCourseId())
                .courseName(course.getCourseName())

                .semesterId(semester.getSemesterId())
                .semesterName(semester.getSemesterName())

                .academicYearId(academicYear.getAcademicYearId())
                .academicYearName(academicYear.getName())

                .division(assignment.getDivision())
                .build();
    }
}
