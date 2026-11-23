package com.campussync.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeeAssignmentResponseDTO {

    private Long feeStructureId;

    private int generatedCount;

    private String message;
}
