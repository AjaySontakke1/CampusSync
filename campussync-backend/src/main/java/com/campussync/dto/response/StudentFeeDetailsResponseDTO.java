package com.campussync.dto.response;

import com.campussync.enums.FeeStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentFeeDetailsResponseDTO {

    private Long feeId;

    private String courseName;
    private String semesterName;
    private String academicYear;

    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal remainingAmount;

    private LocalDate dueDate;
    private FeeStatus status;

    private List<PaymentHistoryResponseDTO> paymentHistory;
}
