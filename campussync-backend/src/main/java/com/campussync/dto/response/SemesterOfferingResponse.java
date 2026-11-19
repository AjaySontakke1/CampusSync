package com.campussync.dto.response;

import com.campussync.enums.SemesterStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SemesterOfferingResponse {

    private Long semesterOfferingId;

    private Long semesterId;
    private String semesterName;
    private int semesterNumber;

    private Long academicYearId;
    private String academicYearName;

    private LocalDate startDate;
    private LocalDate endDate;

    private SemesterStatus status;
}
