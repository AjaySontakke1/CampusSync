package com.campussync.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SemesterOfferingRequest {

    @NotNull(message = "Semester ID is required")
    @Positive(message = "Semester ID must be positive")
    private Long semesterId;

    @NotNull(message = "Academic Year ID is required")
    @Positive(message = "Academic Year ID must be positive")
    private Long academicYearId;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;
}
