package com.campussync.controller.admin;

import com.campussync.dto.response.TeacherSubjectAssignmentHistoryResponse;
import com.campussync.service.TeacherSubjectAssignmentHistoryService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/teacher-subject-assignments")
@RequiredArgsConstructor
public class AdminTeacherSubjectAssignmentHistoryController {

    private final TeacherSubjectAssignmentHistoryService historyService;

    // GET ASSIGNMENT HISTORY
    @GetMapping("/{assignmentId}/history")
    public ResponseEntity<List<TeacherSubjectAssignmentHistoryResponse>>
            getAssignmentHistory(
                    @PathVariable Long assignmentId) {

        List<TeacherSubjectAssignmentHistoryResponse> history =
                historyService.getHistory(assignmentId);

        return ResponseEntity.ok(history);
    }
}
