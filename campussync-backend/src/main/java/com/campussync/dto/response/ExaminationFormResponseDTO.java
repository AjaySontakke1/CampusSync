package com.campussync.dto.response;

import com.campussync.enums.ExaminationFormStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExaminationFormResponseDTO {

    private Long examinationFormId;

    private String examinationName;
    private String semesterName;
    private String academicYear;

    private LocalDateTime submittedAt;

    private ExaminationFormStatus status;
}
