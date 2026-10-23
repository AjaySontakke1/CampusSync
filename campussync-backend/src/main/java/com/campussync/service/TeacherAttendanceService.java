package com.campussync.service;

import com.campussync.dto.AttendanceRecordDTO;
import com.campussync.dto.MarkAttendanceRequest;
import com.campussync.entity.AcademicYear;
import com.campussync.entity.Attendance;
import com.campussync.entity.Student;
import com.campussync.entity.Subject;
import com.campussync.entity.Teacher;
import com.campussync.repository.AcademicYearRepository;
import com.campussync.repository.AttendanceRepository;
import com.campussync.repository.StudentRepository;
import com.campussync.repository.SubjectRepository;
import com.campussync.repository.TeacherRepository;
import com.campussync.repository.TeacherSubjectAssignmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeacherAttendanceService {

    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final AcademicYearRepository academicYearRepository;
    private final AttendanceRepository attendanceRepository;
    private final TeacherSubjectAssignmentRepository assignmentRepository;

    public TeacherAttendanceService(
            TeacherRepository teacherRepository,
            StudentRepository studentRepository,
            SubjectRepository subjectRepository,
            AcademicYearRepository academicYearRepository,
            AttendanceRepository attendanceRepository,
            TeacherSubjectAssignmentRepository assignmentRepository) {
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
        this.academicYearRepository = academicYearRepository;
        this.attendanceRepository = attendanceRepository;
        this.assignmentRepository = assignmentRepository;
    }

    @Transactional
    public void markAttendance(String userEmail, MarkAttendanceRequest request) {
        Teacher teacher = teacherRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Teacher profile not found for email: " + userEmail));

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + request.getStudentId()));

        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new IllegalArgumentException("Subject not found with ID: " + request.getSubjectId()));

        AcademicYear academicYear = academicYearRepository.findById(request.getAcademicYearId())
                .orElseThrow(() -> new IllegalArgumentException("Academic year not found with ID: " + request.getAcademicYearId()));

        if (!academicYear.isActive()) {
            throw new IllegalArgumentException("Academic year is not active");
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
            throw new IllegalArgumentException("Attendance already recorded for this student, subject, and date");
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

        attendanceRepository.save(attendance);
    }

    @Transactional(readOnly = true)
    public List<AttendanceRecordDTO> getAttendanceByTeacherAndDate(String userEmail, LocalDate attendanceDate) {
        Teacher teacher = teacherRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Teacher profile not found for email: " + userEmail));

        List<Attendance> attendances = attendanceRepository.findByTeacherAndAttendanceDate(teacher, attendanceDate);

        return attendances.stream()
                .map(a -> AttendanceRecordDTO.builder()
                        .attendanceId(a.getAttendanceId())
                        .subjectName(a.getSubject().getSubjectName())
                        .attendanceDate(a.getAttendanceDate())
                        .status(a.getStatus().name())
                        .remarks(a.getRemarks())
                        .build())
                .collect(Collectors.toList());
    }
}
