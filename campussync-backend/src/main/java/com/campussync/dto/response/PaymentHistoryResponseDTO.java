package com.campussync.dto.response;

import com.campussync.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentHistoryResponseDTO {

    private Long paymentId;
    private BigDecimal amount;
    private String transactionId;
    private String paymentMethod;
    private LocalDateTime paymentDate;
    private PaymentStatus status;
}
