package com.campussync.service;

import com.campussync.dto.AcademicYearResponse;
import com.campussync.dto.AttendanceResponse;
import com.campussync.dto.StudentSubjectAttendanceResponse;
import com.campussync.entity.AcademicYear;
import com.campussync.entity.Attendance;
import com.campussync.entity.Student;
import com.campussync.enums.AttendanceStatus;
import com.campussync.exception.ResourceNotFoundException;
import com.campussync.repository.AcademicYearRepository;
import com.campussync.repository.AttendanceRepository;
import com.campussync.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StudentAttendanceService {

    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;
    private final AcademicYearRepository academicYearRepository;

    public StudentAttendanceService(
            StudentRepository studentRepository,
            AttendanceRepository attendanceRepository,
            AcademicYearRepository academicYearRepository) {
        this.studentRepository = studentRepository;
        this.attendanceRepository = attendanceRepository;
        this.academicYearRepository = academicYearRepository;
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

    @Transactional(readOnly = true)
    public List<StudentSubjectAttendanceResponse> getSubjectWiseAttendance(
            String studentEmail,
            Long academicYearId) {

        // 1. Find logged-in student
        Student student = getLoggedInStudent(studentEmail);

        // 2. Find selected academic year
        AcademicYear academicYear = academicYearRepository.findById(academicYearId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Academic year not found"));

        // 3. Fetch attendance for selected academic year
        List<Attendance> attendanceList =
                attendanceRepository.findByStudentAndAcademicYear(
                        student, academicYear);

        // 4. Group records by subject
        Map<Long, List<Attendance>> subjectGroups =
                attendanceList.stream()
                        .collect(Collectors.groupingBy(
                                a -> a.getSubject().getSubjectId()
                        ));

        // 5. Calculate subject-wise summary
        return subjectGroups.values().stream()
                .map(records -> {

                    Attendance first = records.get(0);

                    long total = records.size();

                    long present = records.stream()
                            .filter(a -> a.getStatus() == AttendanceStatus.PRESENT)
                            .count();

                    long absent = records.stream()
                            .filter(a -> a.getStatus() == AttendanceStatus.ABSENT)
                            .count();

                    double percentage = total == 0
                            ? 0.0
                            : (present * 100.0) / total;

                    return StudentSubjectAttendanceResponse.builder()
                            .subjectId(first.getSubject().getSubjectId())
                            .subjectName(first.getSubject().getSubjectName())
                            .totalClasses(total)
                            .presentClasses(present)
                            .absentClasses(absent)
                            .attendancePercentage(
                                    Math.round(percentage * 100.0) / 100.0
                            )
                            .build();
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AcademicYearResponse> getAvailableAcademicYears() {

        return academicYearRepository.findAllByOrderByStartDateDesc()
                .stream()
                .map(year -> AcademicYearResponse.builder()
                        .academicYearId(year.getAcademicYearId())
                        .name(year.getName())
                        .startDate(year.getStartDate())
                        .endDate(year.getEndDate())
                        .active(year.isActive())
                        .build())
                .toList();
    }
}
