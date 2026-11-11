package com.campussync.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcademicYearResponse {

    private Long academicYearId;

    private String name;

    private LocalDate startDate;

    private LocalDate endDate;

    private boolean active;
}
