package com.campussync.controller;

import com.campussync.dto.AttendanceResponse;
import com.campussync.dto.MarkAttendanceRequest;
import com.campussync.entity.Attendance;
import com.campussync.service.TeacherAttendanceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teacher/attendance")
public class TeacherAttendanceController {

    private final TeacherAttendanceService attendanceService;

    public TeacherAttendanceController(
            TeacherAttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping
    public ResponseEntity<AttendanceResponse> markAttendance(
            @Valid @RequestBody MarkAttendanceRequest request,
            Authentication authentication) {

        Attendance attendance = attendanceService.markAttendance(
                authentication.getName(), request);

        AttendanceResponse response = AttendanceResponse.builder()
                .attendanceId(attendance.getAttendanceId())
                .studentId(attendance.getStudent().getStudentId())
                .subjectId(attendance.getSubject().getSubjectId())
                .teacherId(attendance.getTeacher().getTeacherId())
                .academicYearId(
                        attendance.getAcademicYear().getAcademicYearId())
                .attendanceDate(attendance.getAttendanceDate())
                .status(attendance.getStatus())
                .remarks(attendance.getRemarks())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
