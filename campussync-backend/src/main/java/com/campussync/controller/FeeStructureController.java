package com.campussync.controller;

import com.campussync.dto.FeeAssignmentResponseDTO;
import com.campussync.dto.FeeStructureRequestDTO;
import com.campussync.dto.FeeStructureResponseDTO;
import com.campussync.service.FeeStructureService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/fee-structures")
@RequiredArgsConstructor
public class FeeStructureController {

    private final FeeStructureService feeStructureService;

    // 1. Create Fee Structure
    @PostMapping
    public ResponseEntity<FeeStructureResponseDTO> createFeeStructure(
            @Valid @RequestBody FeeStructureRequestDTO request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(feeStructureService.createFeeStructure(request));
    }

    // 2. Get All Active Fee Structures
    @GetMapping
    public ResponseEntity<List<FeeStructureResponseDTO>>
            getAllFeeStructures() {

        return ResponseEntity.ok(
                feeStructureService.getAllFeeStructures());
    }

    // 3. Get Fee Structure By ID
    @GetMapping("/{feeStructureId}")
    public ResponseEntity<FeeStructureResponseDTO> getFeeStructureById(
            @PathVariable Long feeStructureId) {

        return ResponseEntity.ok(
                feeStructureService.getFeeStructureById(feeStructureId));
    }

    // 4. Update Fee Structure
    @PutMapping("/{feeStructureId}")
    public ResponseEntity<FeeStructureResponseDTO> updateFeeStructure(
            @PathVariable Long feeStructureId,
            @Valid @RequestBody FeeStructureRequestDTO request) {

        return ResponseEntity.ok(
                feeStructureService.updateFeeStructure(
                        feeStructureId, request));
    }

    // 5. Deactivate Fee Structure
    @DeleteMapping("/{feeStructureId}")
    public ResponseEntity<Void> deactivateFeeStructure(
            @PathVariable Long feeStructureId) {

        feeStructureService.deactivateFeeStructure(feeStructureId);

        return ResponseEntity.noContent().build();
    }

    // 6. Assign Fee Structure to Enrolled Students
    @PostMapping("/{feeStructureId}/assign")
    public ResponseEntity<FeeAssignmentResponseDTO> assignFeeStructure(
            @PathVariable Long feeStructureId) {

        int generatedCount =
                feeStructureService.assignFeeStructureToStudents(
                        feeStructureId);

        FeeAssignmentResponseDTO response =
                FeeAssignmentResponseDTO.builder()
                        .feeStructureId(feeStructureId)
                        .generatedCount(generatedCount)
                        .message(
                                generatedCount > 0
                                        ? "Fee assigned successfully"
                                        : "No new fee records generated")
                        .build();

        return ResponseEntity.ok(response);
    }
}
