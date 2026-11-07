package com.campussync.service;

import com.campussync.dto.AttendanceResponse;
import com.campussync.entity.Attendance;
import com.campussync.entity.Student;
import com.campussync.exception.ResourceNotFoundException;
import com.campussync.repository.AttendanceRepository;
import com.campussync.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentAttendanceService {

    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;

    public StudentAttendanceService(
            StudentRepository studentRepository,
            AttendanceRepository attendanceRepository) {
        this.studentRepository = studentRepository;
        this.attendanceRepository = attendanceRepository;
    }

    @Transactional(readOnly = true)
    public Student getLoggedInStudent(String studentEmail) {

        return studentRepository.findByUserEmail(studentEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student profile not found"));
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponse> getMyAttendance(String studentEmail) {

        // 1. Find logged-in student
        Student student = getLoggedInStudent(studentEmail);

        // 2. Fetch attendance records
        List<Attendance> attendanceList =
                attendanceRepository.findByStudent(student);

        // 3. Convert Entity into DTO
        return attendanceList.stream()
                .map(attendance -> AttendanceResponse.builder()
                        .attendanceId(attendance.getAttendanceId())
                        .studentId(attendance.getStudent().getStudentId())
                        .subjectId(attendance.getSubject().getSubjectId())
                        .teacherId(attendance.getTeacher().getTeacherId())
                        .academicYearId(attendance.getAcademicYear().getAcademicYearId())
                        .attendanceDate(attendance.getAttendanceDate())
                        .status(attendance.getStatus())
                        .remarks(attendance.getRemarks())
                        .build())
                .toList();
    }
}
