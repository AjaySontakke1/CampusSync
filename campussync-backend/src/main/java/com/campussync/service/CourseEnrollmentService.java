package com.campussync.service;

import com.campussync.dto.request.CourseEnrollmentRequest;
import com.campussync.dto.response.CourseEnrollmentResponse;
import com.campussync.entity.AcademicYear;
import com.campussync.entity.Course;
import com.campussync.entity.CourseEnrollment;
import com.campussync.entity.Semester;
import com.campussync.entity.Student;
import com.campussync.repository.AcademicYearRepository;
import com.campussync.repository.CourseEnrollmentRepository;
import com.campussync.repository.CourseRepository;
import com.campussync.repository.SemesterRepository;
import com.campussync.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseEnrollmentService {

    private final CourseEnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final SemesterRepository semesterRepository;
    private final AcademicYearRepository academicYearRepository;

    @Transactional
    public CourseEnrollmentResponse createEnrollment(
            CourseEnrollmentRequest request) {

        // 1. Find Student
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Student not found"));

        // 2. Find Course
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Course not found"));

        // 3. Find Semester
        Semester semester = semesterRepository.findById(request.getSemesterId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Semester not found"));

        // 4. Find Academic Year
        AcademicYear academicYear =
                academicYearRepository.findById(request.getAcademicYearId())
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Academic year not found"));

        // 5. Check active status
        if (!Boolean.TRUE.equals(student.getUser().getActive())
                || !course.isActive()
                || !semester.isActive()
                || !academicYear.isActive()) {

            throw new IllegalArgumentException(
                    "Student, course, semester and academic year must be active");
        }

        // 6. Check Semester belongs to selected Course
        if (!semester.getCourse().getCourseId()
                .equals(course.getCourseId())) {

            throw new IllegalArgumentException(
                    "Selected semester does not belong to this course");
        }

        // 7. Check Student's current academic details
        if (!student.getCourse().getCourseId().equals(course.getCourseId())
                || !student.getSemester().getSemesterId()
                        .equals(semester.getSemesterId())) {

            throw new IllegalArgumentException(
                    "Enrollment details do not match student's current course and semester");
        }

        if (!request.getDivision().trim().equalsIgnoreCase(
            student.getDivision())) {

            throw new IllegalArgumentException(
                    "Division does not match student's current division");
        }

        // 8. Check duplicate active enrollment
        boolean exists = enrollmentRepository
                .existsByStudentAndCourseAndSemesterAndAcademicYearAndDivisionAndActiveTrue(
                        student,
                        course,
                        semester,
                        academicYear,
                        request.getDivision().trim()
                );

        if (exists) {
            throw new IllegalArgumentException(
                    "Student already has an active enrollment for these details");
        }

        // 9. Create enrollment
        CourseEnrollment enrollment = CourseEnrollment.builder()
                .student(student)
                .course(course)
                .semester(semester)
                .academicYear(academicYear)
                .division(request.getDivision().trim())
                .enrollmentDate(LocalDate.now())
                .active(true)
                .build();

        // 10. Save enrollment
        CourseEnrollment saved = enrollmentRepository.save(enrollment);

        // 11. Convert entity to response DTO
        return mapToResponse(saved);
    }

    @Transactional
    public CourseEnrollmentResponse updateEnrollment(
            Long enrollmentId,
            CourseEnrollmentRequest request) {

        // 1. Find existing enrollment
        CourseEnrollment enrollment =
                enrollmentRepository.findById(enrollmentId)
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Enrollment not found"));

        if (!enrollment.isActive()) {
            throw new IllegalArgumentException(
                    "Inactive enrollment cannot be updated");
        }

        // 2. Find requested Student
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Student not found"));

        // 3. Find Course
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Course not found"));

        // 4. Find Semester
        Semester semester = semesterRepository.findById(request.getSemesterId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Semester not found"));

        // 5. Find Academic Year
        AcademicYear academicYear =
                academicYearRepository.findById(request.getAcademicYearId())
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Academic year not found"));

        // 6. Validate active status
        if (!Boolean.TRUE.equals(student.getUser().getActive())
                || !course.isActive()
                || !semester.isActive()
                || !academicYear.isActive()) {

            throw new IllegalArgumentException(
                    "All selected records must be active");
        }

        // 7. Validate Course-Semester relationship
        if (!semester.getCourse().getCourseId()
                .equals(course.getCourseId())) {

            throw new IllegalArgumentException(
                    "Semester does not belong to selected course");
        }

        // 8. Validate Student's current academic details
        if (!student.getCourse().getCourseId().equals(course.getCourseId())
                || !student.getSemester().getSemesterId()
                        .equals(semester.getSemesterId())) {

            throw new IllegalArgumentException(
                    "Enrollment does not match student's academic details");
        }

        if (!request.getDivision().trim()
                .equalsIgnoreCase(student.getDivision())) {

            throw new IllegalArgumentException(
                    "Division does not match student's current division");
        }

        // 9. Check duplicate enrollment excluding current record
        boolean duplicate = enrollmentRepository
                .existsByStudentAndCourseAndSemesterAndAcademicYearAndDivisionAndActiveTrueAndEnrollmentIdNot(
                        student,
                        course,
                        semester,
                        academicYear,
                        request.getDivision().trim(),
                        enrollmentId
                );

        if (duplicate) {
            throw new IllegalArgumentException(
                    "Duplicate active enrollment exists");
        }

        // 10. Update existing record
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setSemester(semester);
        enrollment.setAcademicYear(academicYear);
        enrollment.setDivision(request.getDivision().trim());

        CourseEnrollment updated =
                enrollmentRepository.save(enrollment);

        return mapToResponse(updated);
    }

    @Transactional
    public void deactivateEnrollment(Long enrollmentId) {

        // 1. Find enrollment
        CourseEnrollment enrollment =
                enrollmentRepository.findById(enrollmentId)
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Enrollment not found: " + enrollmentId));

        // 2. Check current status
        if (!enrollment.isActive()) {
            throw new IllegalArgumentException(
                    "Enrollment is already inactive");
        }

        // 3. Deactivate enrollment
        enrollment.setActive(false);

        // 4. Save changes
        enrollmentRepository.save(enrollment);
    }

    @Transactional(readOnly = true)
    public List<CourseEnrollmentResponse> getAllEnrollments() {

        return enrollmentRepository.findByActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CourseEnrollmentResponse getEnrollmentById(Long enrollmentId) {

        CourseEnrollment enrollment =
                enrollmentRepository.findByEnrollmentIdAndActiveTrue(enrollmentId)
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Active enrollment not found: " + enrollmentId));

        return mapToResponse(enrollment);
    }

    @Transactional(readOnly = true)
    public List<CourseEnrollmentResponse> getStudentEnrollments(
            Long studentId) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Student not found"));

        return enrollmentRepository.findByStudentAndActiveTrue(student)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CourseEnrollmentResponse> getClassEnrollments(
            Long courseId,
            Long semesterId,
            Long academicYearId,
            String division) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Course not found"));

        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Semester not found"));

        AcademicYear academicYear =
                academicYearRepository.findById(academicYearId)
                        .orElseThrow(() ->
                                new EntityNotFoundException("Academic year not found"));

        return enrollmentRepository
                .findByCourseAndSemesterAndAcademicYearAndDivisionAndActiveTrue(
                        course, semester, academicYear, division.trim())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private CourseEnrollmentResponse mapToResponse(
            CourseEnrollment enrollment) {

        Student student = enrollment.getStudent();

        return CourseEnrollmentResponse.builder()
                .enrollmentId(enrollment.getEnrollmentId())

                .studentId(student.getStudentId())
                .studentName(
                        student.getUser().getFirstName() + " " +
                        student.getUser().getLastName())
                .rollNumber(student.getRollNumber())

                .courseId(enrollment.getCourse().getCourseId())
                .courseName(enrollment.getCourse().getCourseName())

                .semesterId(enrollment.getSemester().getSemesterId())
                .semesterName(enrollment.getSemester().getSemesterName())

                .academicYearId(
                        enrollment.getAcademicYear().getAcademicYearId())
                .academicYearName(enrollment.getAcademicYear().getName())

                .division(enrollment.getDivision())
                .enrollmentDate(enrollment.getEnrollmentDate())
                .active(enrollment.isActive())
                .build();
    }
}
