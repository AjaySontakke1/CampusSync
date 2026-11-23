package com.campussync.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamFeeEligibilityResponseDTO {

    private Long studentId;

    private boolean eligible;

    private BigDecimal totalFee;
    private BigDecimal paidAmount;
    private BigDecimal remainingAmount;

    private String message;
}
