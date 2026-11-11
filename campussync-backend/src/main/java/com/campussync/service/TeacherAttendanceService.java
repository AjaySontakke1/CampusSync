package com.campussync.service;

import com.campussync.dto.AttendanceResponse;
import com.campussync.dto.AttendanceStudentResponse;
import com.campussync.dto.AttendanceSummaryResponse;
import com.campussync.dto.BulkAttendanceRequest;
import com.campussync.dto.MarkAttendanceRequest;
import com.campussync.dto.StudentAttendanceRequest;
import com.campussync.dto.UpdateAttendanceRequest;
import com.campussync.entity.AcademicYear;
import com.campussync.entity.Attendance;
import com.campussync.entity.CourseEnrollment;
import com.campussync.entity.Student;
import com.campussync.entity.Subject;
import com.campussync.entity.Teacher;
import com.campussync.entity.TeacherSubjectAssignment;
import com.campussync.enums.AttendanceStatus;
import com.campussync.exception.DuplicateAttendanceException;
import com.campussync.exception.ResourceNotFoundException;
import com.campussync.repository.AcademicYearRepository;
import com.campussync.repository.AttendanceRepository;
import com.campussync.repository.CourseEnrollmentRepository;
import com.campussync.repository.StudentRepository;
import com.campussync.repository.SubjectRepository;
import com.campussync.repository.TeacherRepository;
import com.campussync.repository.TeacherSubjectAssignmentRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TeacherAttendanceService {

    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final AcademicYearRepository academicYearRepository;
    private final AttendanceRepository attendanceRepository;
    private final TeacherSubjectAssignmentRepository assignmentRepository;
    private final CourseEnrollmentRepository courseEnrollmentRepository;

    public TeacherAttendanceService(
            TeacherRepository teacherRepository,
            StudentRepository studentRepository,
            SubjectRepository subjectRepository,
            AcademicYearRepository academicYearRepository,
            AttendanceRepository attendanceRepository,
            TeacherSubjectAssignmentRepository assignmentRepository,
            CourseEnrollmentRepository courseEnrollmentRepository) {
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
        this.academicYearRepository = academicYearRepository;
        this.attendanceRepository = attendanceRepository;
        this.assignmentRepository = assignmentRepository;
        this.courseEnrollmentRepository = courseEnrollmentRepository;
    }

    @Transactional
    public Attendance markAttendance(String userEmail, MarkAttendanceRequest request) {
        Teacher teacher = teacherRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher profile not found for email: " + userEmail));

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + request.getStudentId()));

        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with ID: " + request.getSubjectId()));

        AcademicYear academicYear = academicYearRepository.findById(request.getAcademicYearId())
                .orElseThrow(() -> new ResourceNotFoundException("Academic year not found with ID: " + request.getAcademicYearId()));

        if (!academicYear.isActive()) {
            throw new IllegalArgumentException("Academic year is not active");
        }

        if (!subject.isActive()) {
            throw new IllegalArgumentException("Subject is not active");
        }

        if (request.getAttendanceDate().isBefore(academicYear.getStartDate())
                || request.getAttendanceDate().isAfter(academicYear.getEndDate())) {
            throw new IllegalArgumentException("Attendance date must be within the academic year");
        }

        if (!student.getSemester().getSemesterId()
                .equals(subject.getSemester().getSemesterId())) {
            throw new IllegalArgumentException("Student is not enrolled in this subject's semester");
        }

        if (!student.getCourse().getCourseId()
                .equals(subject.getSemester().getCourse().getCourseId())) {
            throw new IllegalArgumentException("Subject does not belong to the student's course");
        }

        boolean assigned = assignmentRepository
                .existsByTeacherAndSubjectAndCourseAndSemesterAndAcademicYear(
                        teacher,
                        subject,
                        student.getCourse(),
                        student.getSemester(),
                        academicYear);

        if (!assigned) {
            throw new IllegalArgumentException("Teacher is not assigned to this subject and course");
        }

        boolean alreadyRecorded = attendanceRepository
                .existsByStudentAndSubjectAndAcademicYearAndAttendanceDate(
                        student,
                        subject,
                        academicYear,
                        request.getAttendanceDate());

        if (alreadyRecorded) {
            throw new DuplicateAttendanceException("Attendance already recorded for this student, subject, and date");
        }

        Attendance attendance = Attendance.builder()
                .student(student)
                .subject(subject)
                .teacher(teacher)
                .academicYear(academicYear)
                .attendanceDate(request.getAttendanceDate())
                .status(request.getStatus())
                .remarks(request.getRemarks())
                .build();

        return attendanceRepository.save(attendance);
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponse> getAttendanceByDate(
            String teacherEmail,
            LocalDate attendanceDate) {

        Teacher teacher = teacherRepository.findByUserEmail(teacherEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Teacher profile not found"));

        return attendanceRepository
                .findByTeacherAndAttendanceDate(teacher, attendanceDate)
                .stream()
                .map(attendance -> AttendanceResponse.builder()
                        .attendanceId(attendance.getAttendanceId())
                        .studentId(attendance.getStudent().getStudentId())
                        .subjectId(attendance.getSubject().getSubjectId())
                        .teacherId(attendance.getTeacher().getTeacherId())
                        .academicYearId(
                                attendance.getAcademicYear().getAcademicYearId())
                        .attendanceDate(attendance.getAttendanceDate())
                        .status(attendance.getStatus())
                        .remarks(attendance.getRemarks())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponse> getAttendanceBySubject(
            String teacherEmail,
            Long subjectId,
            LocalDate attendanceDate) {

        Teacher teacher = teacherRepository.findByUserEmail(teacherEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Teacher profile not found"));

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Subject not found"));

        return attendanceRepository
                .findByTeacherAndSubjectAndAttendanceDate(
                        teacher, subject, attendanceDate)
                .stream()
                .map(attendance -> AttendanceResponse.builder()
                        .attendanceId(attendance.getAttendanceId())
                        .studentId(attendance.getStudent().getStudentId())
                        .subjectId(attendance.getSubject().getSubjectId())
                        .teacherId(attendance.getTeacher().getTeacherId())
                        .academicYearId(
                                attendance.getAcademicYear().getAcademicYearId())
                        .attendanceDate(attendance.getAttendanceDate())
                        .status(attendance.getStatus())
                        .remarks(attendance.getRemarks())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public AttendanceSummaryResponse getAttendanceSummary(
            String teacherEmail,
            Long subjectId,
            LocalDate attendanceDate) {

        // 1. Find logged-in teacher
        Teacher teacher = teacherRepository.findByUserEmail(teacherEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Teacher profile not found"));

        // 2. Find subject
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Subject not found"));

        // 3. Count total attendance records
        long total = attendanceRepository
                .countByTeacherAndSubjectAndAttendanceDate(
                        teacher, subject, attendanceDate);

        // 4. Count present students
        long present = attendanceRepository
                .countByTeacherAndSubjectAndAttendanceDateAndStatus(
                        teacher, subject, attendanceDate,
                        AttendanceStatus.PRESENT);

        // 5. Count absent students
        long absent = attendanceRepository
                .countByTeacherAndSubjectAndAttendanceDateAndStatus(
                        teacher, subject, attendanceDate,
                        AttendanceStatus.ABSENT);

        // 6. Calculate percentage
        double percentage = total == 0
                ? 0.0
                : (present * 100.0) / total;

        // 7. Return summary
        return AttendanceSummaryResponse.builder()
                .subjectId(subject.getSubjectId())
                .subjectName(subject.getSubjectName())
                .totalStudents(total)
                .presentStudents(present)
                .absentStudents(absent)
                .attendancePercentage(
                        Math.round(percentage * 100.0) / 100.0)
                .build();
    }

    @Transactional
    public Attendance updateAttendance(
            String teacherEmail,
            Long attendanceId,
            UpdateAttendanceRequest request) {

        // 1. Find logged-in teacher
        Teacher teacher = teacherRepository.findByUserEmail(teacherEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Teacher profile not found"));

        // 2. Find attendance record
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Attendance record not found"));

        // 3. Check whether this attendance belongs to logged-in teacher
        if (!attendance.getTeacher().getTeacherId()
                .equals(teacher.getTeacherId())) {
            throw new AccessDeniedException(
                    "You cannot update another teacher's attendance");
        }

        // 4. Update status and remarks
        attendance.setStatus(request.getStatus());
        attendance.setRemarks(request.getRemarks());

        // 5. Save updated attendance
        return attendanceRepository.save(attendance);
    }

    @Transactional
    public void deleteAttendance(String teacherEmail, Long attendanceId) {

        // 1. Find logged-in teacher
        Teacher teacher = teacherRepository.findByUserEmail(teacherEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Teacher profile not found"));

        // 2. Find attendance record
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Attendance record not found"));

        // 3. Verify ownership
        if (!attendance.getTeacher().getTeacherId()
                .equals(teacher.getTeacherId())) {

            throw new AccessDeniedException(
                    "You cannot delete another teacher's attendance");
        }

        // 4. Delete attendance
        attendanceRepository.delete(attendance);
    }

    @Transactional(readOnly = true)
    public List<AttendanceStudentResponse> getStudentsForAttendance(
            String teacherEmail,
            Long assignmentId) {

        // 1. Find the logged-in teacher
        Teacher teacher = teacherRepository.findByUserEmail(teacherEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Teacher profile not found"));

        // 2. Find the assignment and verify teacher ownership
        TeacherSubjectAssignment assignment = assignmentRepository
                .findByAssignmentIdAndTeacher(assignmentId, teacher)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Teacher assignment not found"));

        // 3. Fetch active enrollments for this exact class
        List<CourseEnrollment> enrollments =
                courseEnrollmentRepository
                        .findByCourseAndSemesterAndAcademicYearAndDivisionAndActiveTrue(
                                assignment.getCourse(),
                                assignment.getSemester(),
                                assignment.getAcademicYear(),
                                assignment.getDivision()
                        );

        // 4. Convert enrollments into response DTOs
        return enrollments.stream()
                .map(enrollment -> {
                    Student student = enrollment.getStudent();

                    return AttendanceStudentResponse.builder()
                            .studentId(student.getStudentId())
                            .rollNumber(student.getRollNumber())
                            .registrationNumber(student.getRegistrationNumber())
                            .firstName(student.getUser().getFirstName())
                            .lastName(student.getUser().getLastName())
                            .build();
                })
                .toList();
    }
}
