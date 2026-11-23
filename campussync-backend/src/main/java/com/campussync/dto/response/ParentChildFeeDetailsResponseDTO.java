package com.campussync.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParentChildFeeDetailsResponseDTO {

    private Long studentId;
    private String studentName;
    private String registrationNumber;

    private List<StudentFeeDetailsResponseDTO> feeDetails;
}
