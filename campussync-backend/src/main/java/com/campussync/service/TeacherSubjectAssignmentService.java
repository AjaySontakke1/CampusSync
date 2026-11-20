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

        // 10. Check duplicate active assignment
        boolean alreadyAssigned =
                assignmentRepository
                        .existsBySubjectAndCourseAndSemesterAndAcademicYearAndDivisionAndActiveTrue(
                                subject, course, semester, academicYear, division);

        if (alreadyAssigned) {
            throw new IllegalArgumentException(
                    "An active teacher is already assigned to this subject and division");
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
                        .active(true)
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

    // UPDATE ASSIGNMENT
    public TeacherSubjectAssignmentResponse updateAssignment(
            Long assignmentId,
            TeacherSubjectAssignmentRequest request) {

        // Find existing assignment
        TeacherSubjectAssignment assignment =
                assignmentRepository.findById(assignmentId)
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Assignment not found"));

        if (!assignment.isActive()) {
            throw new IllegalArgumentException(
                    "Cannot update an inactive assignment");
        }

        // Find teacher
        Teacher teacher = teacherRepository.findById(request.getTeacherId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Teacher not found"));

        if (!Boolean.TRUE.equals(teacher.getUser().getActive())) {
            throw new IllegalArgumentException("Teacher is inactive");
        }

        // Find subject
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Subject not found"));

        if (!subject.isActive()) {
            throw new IllegalArgumentException("Subject is inactive");
        }

        // Find course
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Course not found"));

        if (!course.isActive()) {
            throw new IllegalArgumentException("Course is inactive");
        }

        // Find semester
        Semester semester = semesterRepository.findById(request.getSemesterId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Semester not found"));

        if (!semester.isActive()) {
            throw new IllegalArgumentException("Semester is inactive");
        }

        // Validate course-semester relationship
        if (!semester.getCourse().getCourseId().equals(course.getCourseId())) {
            throw new IllegalArgumentException(
                    "Semester does not belong to selected course");
        }

        // Validate subject-semester relationship
        if (!subject.getSemester().getSemesterId()
                .equals(semester.getSemesterId())) {
            throw new IllegalArgumentException(
                    "Subject does not belong to selected semester");
        }

        // Find academic year
        AcademicYear academicYear =
                academicYearRepository.findById(request.getAcademicYearId())
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Academic year not found"));

        if (!academicYear.isActive()) {
            throw new IllegalArgumentException(
                    "Academic year is inactive");
        }

        String division = request.getDivision().trim().toUpperCase();

        // Check duplicate, excluding the current assignment
        boolean duplicate =
                assignmentRepository
                        .existsBySubjectAndCourseAndSemesterAndAcademicYearAndDivisionAndActiveTrue(
                                subject, course, semester, academicYear, division);

        if (duplicate &&
                !(assignment.getSubject().getSubjectId().equals(subject.getSubjectId())
                && assignment.getCourse().getCourseId().equals(course.getCourseId())
                && assignment.getSemester().getSemesterId().equals(semester.getSemesterId())
                && assignment.getAcademicYear().getAcademicYearId()
                        .equals(academicYear.getAcademicYearId())
                && assignment.getDivision().equalsIgnoreCase(division))) {

            throw new IllegalArgumentException(
                    "Another active assignment already exists for this subject and division");
        }

        // Update assignment fields
        assignment.setTeacher(teacher);
        assignment.setSubject(subject);
        assignment.setCourse(course);
        assignment.setSemester(semester);
        assignment.setAcademicYear(academicYear);
        assignment.setDivision(division);

        TeacherSubjectAssignment updated =
                assignmentRepository.save(assignment);

        return mapToResponse(updated);
    }

    // DEACTIVATE ASSIGNMENT
    public void deactivateAssignment(Long assignmentId) {

        TeacherSubjectAssignment assignment =
                assignmentRepository.findById(assignmentId)
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Assignment not found"));

        if (!assignment.isActive()) {
            throw new IllegalArgumentException(
                    "Assignment is already inactive");
        }

        assignment.setActive(false);

        assignmentRepository.save(assignment);
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
                .active(assignment.isActive())
                .build();
    }
}
