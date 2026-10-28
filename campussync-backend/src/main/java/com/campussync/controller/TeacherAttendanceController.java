package com.campussync.controller;

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
    public ResponseEntity<Attendance> markAttendance(
            @Valid @RequestBody MarkAttendanceRequest request,
            Authentication authentication) {

        Attendance attendance = attendanceService.markAttendance(
                authentication.getName(), request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(attendance);
    }
}
