package com.campussync.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FeeConcessionDecisionDTO {

    @NotBlank
    private String decision;

    @Size(max = 500)
    private String adminRemarks;
}
