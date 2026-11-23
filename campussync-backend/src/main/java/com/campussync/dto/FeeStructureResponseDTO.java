package com.campussync.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeeStructureResponseDTO {

    private Long feeStructureId;

    private Long courseId;
    private String courseName;

    private Long semesterId;
    private String semesterName;

    private Long academicYearId;
    private String academicYearName;

    private BigDecimal amount;

    private LocalDate dueDate;

    private boolean active;
}
