package com.campussync.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class FeeConcessionRequestDTO {

    @NotNull
    @Positive
    private Long feeId;

    @NotNull
    @DecimalMin("0.01")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal amount;

    @NotBlank
    @Size(max = 500)
    private String reason;
}
