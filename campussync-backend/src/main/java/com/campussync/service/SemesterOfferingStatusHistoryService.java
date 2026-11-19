package com.campussync.service;

import com.campussync.dto.response.SemesterOfferingStatusHistoryResponse;
import com.campussync.entity.SemesterOffering;
import com.campussync.entity.SemesterOfferingStatusHistory;
import com.campussync.entity.User;
import com.campussync.enums.SemesterStatus;
import com.campussync.repository.SemesterOfferingStatusHistoryRepository;
import com.campussync.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SemesterOfferingStatusHistoryService {

    private final SemesterOfferingStatusHistoryRepository historyRepository;
    private final UserRepository userRepository;

    @Transactional
    public void recordStatusChange(
            SemesterOffering offering,
            SemesterStatus previousStatus,
            SemesterStatus newStatus,
            String adminEmail,
            String reason
    ) {

        // Find the admin who performed the operation
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException("Admin not found")
                );

        // Create history record
        SemesterOfferingStatusHistory history =
                SemesterOfferingStatusHistory.builder()
                        .semesterOffering(offering)
                        .previousStatus(previousStatus)
                        .newStatus(newStatus)
                        .changedBy(admin)
                        .reason(reason)
                        .build();

        // Save history in database
        historyRepository.save(history);
    }

    @Transactional(readOnly = true)
    public List<SemesterOfferingStatusHistoryResponse> getHistory(
            SemesterOffering offering
    ) {
        return historyRepository
                .findBySemesterOfferingOrderByChangedAtDesc(offering)
                .stream()
                .map(history -> SemesterOfferingStatusHistoryResponse.builder()
                        .historyId(history.getHistoryId())
                        .previousStatus(history.getPreviousStatus())
                        .newStatus(history.getNewStatus())
                        .changedBy(
                                history.getChangedBy().getEmail()
                        )
                        .reason(history.getReason())
                        .changedAt(history.getChangedAt())
                        .build())
                .toList();
    }
}
