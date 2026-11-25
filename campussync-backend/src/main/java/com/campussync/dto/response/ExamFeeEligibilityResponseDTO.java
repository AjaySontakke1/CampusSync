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

    private String courseName;
    private String semesterName;
    private String academicYear;

    private BigDecimal currentSemesterOutstanding;
    private BigDecimal previousArrears;
    private BigDecimal totalOutstanding;

    private boolean eligible;

    private String message;
}
