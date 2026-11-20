package com.campussync.service;

import com.campussync.entity.*;
import com.campussync.enums.AssignmentHistoryAction;
import com.campussync.repository.*;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeacherSubjectAssignmentHistoryService {

    private final TeacherSubjectAssignmentHistoryRepository historyRepository;
    private final TeacherSubjectAssignmentRepository assignmentRepository;
    private final UserRepository userRepository;

    // RECORD HISTORY
    @Transactional
    public void recordHistory(
            Long assignmentId,
            String adminEmail,
            AssignmentHistoryAction action,
            String details,
            String reason) {

        // Find assignment
        TeacherSubjectAssignment assignment =
                assignmentRepository.findById(assignmentId)
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Assignment not found"));

        // Find admin using authenticated email
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Admin user not found"));

        // Create history record
        TeacherSubjectAssignmentHistory history =
                TeacherSubjectAssignmentHistory.builder()
                        .assignment(assignment)
                        .action(action)
                        .changedBy(admin)
                        .details(details)
                        .reason(reason)
                        .build();

        // Save history
        historyRepository.save(history);
    }

    // GET HISTORY BY ASSIGNMENT
    @Transactional(readOnly = true)
    public List<TeacherSubjectAssignmentHistory> getHistory(
            Long assignmentId) {

        TeacherSubjectAssignment assignment =
                assignmentRepository.findById(assignmentId)
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Assignment not found"));

        return historyRepository
                .findByAssignmentOrderByChangedAtDesc(assignment);
    }
}
