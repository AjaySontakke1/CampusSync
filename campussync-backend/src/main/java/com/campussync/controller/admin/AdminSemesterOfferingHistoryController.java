package com.campussync.controller.admin;

import com.campussync.dto.response.SemesterOfferingStatusHistoryResponse;
import com.campussync.entity.SemesterOffering;
import com.campussync.repository.SemesterOfferingRepository;
import com.campussync.service.SemesterOfferingStatusHistoryService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/semester-offerings")
@RequiredArgsConstructor
public class AdminSemesterOfferingHistoryController {

    private final SemesterOfferingRepository offeringRepository;
    private final SemesterOfferingStatusHistoryService historyService;

    @GetMapping("/{offeringId}/history")
    public ResponseEntity<List<SemesterOfferingStatusHistoryResponse>>
    getOfferingHistory(@PathVariable Long offeringId) {

        SemesterOffering offering = offeringRepository.findById(offeringId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Semester offering not found"
                        )
                );

        return ResponseEntity.ok(
                historyService.getHistory(offering)
        );
    }
}
