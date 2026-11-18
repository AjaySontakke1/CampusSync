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

    private Long subjectId;

    private String subjectName;

    private String subjectCode;

    private boolean active;
}
