package com.campussync.controller;

import com.campussync.dto.AttendanceResponse;
import com.campussync.dto.StudentSubjectAttendanceResponse;
import com.campussync.service.StudentAttendanceService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student/attendance")
public class StudentAttendanceController {

    private final StudentAttendanceService attendanceService;

    public StudentAttendanceController(
            StudentAttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping
    public ResponseEntity<List<AttendanceResponse>> getMyAttendance(
            Authentication authentication) {

        List<AttendanceResponse> attendance =
                attendanceService.getMyAttendance(
                        authentication.getName());

        return ResponseEntity.ok(attendance);
    }

    @GetMapping("/summary")
    public ResponseEntity<List<StudentSubjectAttendanceResponse>>
            getSubjectWiseAttendance(
                    @RequestParam Long academicYearId,
                    Authentication authentication) {

        List<StudentSubjectAttendanceResponse> summary =
                attendanceService.getSubjectWiseAttendance(
                        authentication.getName(),
                        academicYearId
                );

        return ResponseEntity.ok(summary);
    }
}
