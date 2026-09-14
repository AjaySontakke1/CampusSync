package com.campussync.controller;

import com.campussync.dto.AnnouncementResponseDTO;
import com.campussync.dto.AssignmentProgressDTO;
import com.campussync.dto.AttendanceResponseDTO;
import com.campussync.dto.ExamResultResponseDTO;
import com.campussync.dto.FeeResponseDTO;
import com.campussync.dto.LeaveRequestResponseDTO;
import com.campussync.dto.LectureNoteResponseDTO;
import com.campussync.dto.StudentResponseDTO;
import com.campussync.dto.TimetableResponseDTO;
import com.campussync.service.ParentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/parent")
public class ParentController {

    private final ParentService parentService;

    public ParentController(ParentService parentService) {
        this.parentService = parentService;
    }

    @GetMapping("/students")
    public ResponseEntity<List<StudentResponseDTO>> getMyStudents(Authentication authentication) {
        String email = authentication.getName();
        List<StudentResponseDTO> students = parentService.getMyStudents(email);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/students/{studentId}")
    public ResponseEntity<StudentResponseDTO> getMyStudent(
            @PathVariable Long studentId,
            Authentication authentication) {
        String email = authentication.getName();
        StudentResponseDTO student = parentService.getMyStudent(email, studentId);
        return ResponseEntity.ok(student);
    }

    @GetMapping("/students/{studentId}/attendance")
    public ResponseEntity<AttendanceResponseDTO> getChildAttendance(
            @PathVariable Long studentId,
            Authentication authentication) {
        String email = authentication.getName();
        AttendanceResponseDTO attendance = parentService.getChildAttendance(email, studentId);
        return ResponseEntity.ok(attendance);
    }

    @GetMapping("/students/{studentId}/results")
    public ResponseEntity<List<ExamResultResponseDTO>> getChildExamResults(
            @PathVariable Long studentId,
            Authentication authentication) {
        String email = authentication.getName();
        List<ExamResultResponseDTO> results = parentService.getChildExamResults(email, studentId);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/students/{studentId}/fees")
    public ResponseEntity<List<FeeResponseDTO>> getChildFees(
            @PathVariable Long studentId,
            Authentication authentication) {
        String email = authentication.getName();
        List<FeeResponseDTO> fees = parentService.getChildFees(email, studentId);
        return ResponseEntity.ok(fees);
    }

    @GetMapping("/students/{studentId}/assignments")
    public ResponseEntity<List<AssignmentProgressDTO>> getChildAssignmentProgress(
            @PathVariable Long studentId,
            Authentication authentication) {
        String email = authentication.getName();
        List<AssignmentProgressDTO> assignments = parentService.getChildAssignmentProgress(email, studentId);
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/students/{studentId}/lecture-notes")
    public ResponseEntity<List<LectureNoteResponseDTO>> getChildLectureNotes(
            @PathVariable Long studentId,
            Authentication authentication) {
        String email = authentication.getName();
        List<LectureNoteResponseDTO> notes = parentService.getChildLectureNotes(email, studentId);
        return ResponseEntity.ok(notes);
    }

    @GetMapping("/students/{studentId}/timetable")
    public ResponseEntity<List<TimetableResponseDTO>> getChildTimetable(
            @PathVariable Long studentId,
            Authentication authentication) {
        String email = authentication.getName();
        List<TimetableResponseDTO> timetable = parentService.getChildTimetable(email, studentId);
        return ResponseEntity.ok(timetable);
    }

    @GetMapping("/announcements")
    public ResponseEntity<List<AnnouncementResponseDTO>> getAnnouncements(
            Authentication authentication) {
        String email = authentication.getName();
        List<AnnouncementResponseDTO> announcements = parentService.getActiveAnnouncements(email);
        return ResponseEntity.ok(announcements);
    }

    @GetMapping("/students/{studentId}/leave-requests")
    public ResponseEntity<List<LeaveRequestResponseDTO>> getChildLeaveRequests(
            @PathVariable Long studentId,
            Authentication authentication) {
        String email = authentication.getName();
        List<LeaveRequestResponseDTO> leaveRequests = parentService.getChildLeaveRequests(email, studentId);
        return ResponseEntity.ok(leaveRequests);
    }
}
