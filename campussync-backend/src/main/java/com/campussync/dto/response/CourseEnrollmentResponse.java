package com.campussync.dto.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseEnrollmentResponse {

    private Long enrollmentId;

    private Long studentId;
    private String studentName;
    private String rollNumber;

    private Long courseId;
    private String courseName;

    private Long semesterId;
    private String semesterName;

    private Long academicYearId;
    private String academicYearName;

    private String division;
    private LocalDate enrollmentDate;
    private Boolean active;
}
