package com.campussync.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SemesterSubjectAssignmentResponse {

    private Long assignmentId;

    private Long semesterOfferingId;

    private String semesterName;

    private String academicYearName;

    private Long subjectId;

    private String subjectName;

    private String subjectCode;

    private int credits;

    private boolean active;
}
