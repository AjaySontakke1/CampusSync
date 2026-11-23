package com.campussync.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeeStructureRequestDTO {

    @NotNull
    @Positive
    private Long courseId;

    // Optional for course-wide fee
    @Positive
    private Long semesterId;

    @NotNull
    @Positive
    private Long academicYearId;

    @NotNull
    @DecimalMin("0.01")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal amount;

    @NotNull
    private LocalDate dueDate;
}
