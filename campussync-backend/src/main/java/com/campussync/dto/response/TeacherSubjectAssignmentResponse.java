package com.campussync.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherSubjectAssignmentResponse {

    private Long assignmentId;

    private Long teacherId;
    private String teacherName;

    private Long subjectId;
    private String subjectName;
    private String subjectCode;

    private Long courseId;
    private String courseName;

    private Long semesterId;
    private String semesterName;

    private Long academicYearId;
    private String academicYearName;

    private String division;

    private boolean active;
}
