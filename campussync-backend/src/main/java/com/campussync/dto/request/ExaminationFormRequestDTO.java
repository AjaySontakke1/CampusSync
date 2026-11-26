package com.campussync.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ExaminationFormRequestDTO {

    @NotNull
    @Positive
    private Long examinationSessionId;
}
