package com.campussync.dto.response;

import com.campussync.enums.ExamSessionStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExaminationSessionResponseDTO {

    private Long examinationSessionId;
    private String examinationName;

    private Long semesterId;
    private String semesterName;

    private Long academicYearId;
    private String academicYear;

    private LocalDate formStartDate;
    private LocalDate formEndDate;

    private ExamSessionStatus status;
}
