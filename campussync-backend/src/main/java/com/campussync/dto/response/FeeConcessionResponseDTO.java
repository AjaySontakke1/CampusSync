package com.campussync.dto.response;

import com.campussync.enums.FeeConcessionStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeConcessionResponseDTO {

    private Long concessionId;
    private Long feeId;
    private Long studentId;

    private BigDecimal amount;
    private String reason;

    private FeeConcessionStatus status;

    private String approvedBy;
    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;

    private String adminRemarks;
}
