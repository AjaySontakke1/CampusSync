package com.campussync.controller;

import com.campussync.dto.AnnouncementResponseDTO;
import com.campussync.dto.AssignmentProgressDTO;
import com.campussync.dto.AttendanceResponseDTO;
import com.campussync.dto.ExamResultResponseDTO;
import com.campussync.dto.FeeResponseDTO;
import com.campussync.dto.request.FeeConcessionRequestDTO;
import com.campussync.dto.response.FeeConcessionResponseDTO;
import com.campussync.dto.response.ExamFeeEligibilityResponseDTO;
import com.campussync.dto.response.StudentFeeDetailsResponseDTO;
import com.campussync.dto.LectureNoteResponseDTO;
import com.campussync.dto.LeaveRequestResponseDTO;
import com.campussync.dto.TimetableResponseDTO;
import com.campussync.service.FeeConcessionService;
import com.campussync.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    private final StudentService studentService;
    private final FeeConcessionService feeConcessionService;

    public StudentController(StudentService studentService, FeeConcessionService feeConcessionService) {
        this.studentService = studentService;
        this.feeConcessionService = feeConcessionService;
    }

    @GetMapping("/assignments")
    public ResponseEntity<List<AssignmentProgressDTO>> getMyAssignments(
            Authentication authentication) {

        String email = authentication.getName();

        List<AssignmentProgressDTO> assignments =
                studentService.getMyAssignments(email);

        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/results")
    public ResponseEntity<List<ExamResultResponseDTO>> getMyExamResults(
            Authentication authentication) {

        String email = authentication.getName();

        List<ExamResultResponseDTO> results =
                studentService.getMyExamResults(email);

        return ResponseEntity.ok(results);
    }

    @GetMapping("/fees")
    public ResponseEntity<List<StudentFeeDetailsResponseDTO>> getMyFees(
            Authentication authentication) {

        String email = authentication.getName();

        List<StudentFeeDetailsResponseDTO> fees =
                studentService.getMyFeeDetails(email);

        return ResponseEntity.ok(fees);
    }

    @PostMapping("/fees/concessions")
    public ResponseEntity<FeeConcessionResponseDTO> requestFeeConcession(
            @Valid @RequestBody FeeConcessionRequestDTO request,
            Authentication authentication) {

        String email = authentication.getName();

        FeeConcessionResponseDTO response =
                feeConcessionService.requestConcession(request, email);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/exam-fee-eligibility")
    public ResponseEntity<ExamFeeEligibilityResponseDTO> getMyExamFeeEligibility(
            Authentication authentication) {

        String email = authentication.getName();

        ExamFeeEligibilityResponseDTO eligibility =
                studentService.checkMyExamFeeEligibility(email);

        return ResponseEntity.ok(eligibility);
    }

    @GetMapping("/lecture-notes")
    public ResponseEntity<List<LectureNoteResponseDTO>> getMyLectureNotes(
            Authentication authentication) {

        String email = authentication.getName();

        List<LectureNoteResponseDTO> notes =
                studentService.getMyLectureNotes(email);

        return ResponseEntity.ok(notes);
    }

    @GetMapping("/timetable")
    public ResponseEntity<List<TimetableResponseDTO>> getMyTimetable(
            Authentication authentication) {

        String email = authentication.getName();

        List<TimetableResponseDTO> timetable =
                studentService.getMyTimetable(email);

        return ResponseEntity.ok(timetable);
    }

    @GetMapping("/leave-requests")
    public ResponseEntity<List<LeaveRequestResponseDTO>> getMyLeaveRequests(
            Authentication authentication) {

        String email = authentication.getName();

        List<LeaveRequestResponseDTO> leaveRequests =
                studentService.getMyLeaveRequests(email);

        return ResponseEntity.ok(leaveRequests);
    }

    @GetMapping("/announcements")
    public ResponseEntity<List<AnnouncementResponseDTO>> getMyAnnouncements() {

        List<AnnouncementResponseDTO> announcements =
                studentService.getMyAnnouncements();

        return ResponseEntity.ok(announcements);
    }
}
