package com.campussync.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class ExaminationSessionRequestDTO {

    @NotBlank
    @Size(max = 150)
    private String examinationName;

    @NotNull
    @Positive
    private Long semesterId;

    @NotNull
    @Positive
    private Long academicYearId;

    @NotNull
    private LocalDate formStartDate;

    @NotNull
    private LocalDate formEndDate;
}
