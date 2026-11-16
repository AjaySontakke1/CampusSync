package com.campussync.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseResponse {

    private Long courseId;

    private String courseName;

    private String courseCode;

    private int durationYears;

    private int totalSemesters;

    private boolean active;

    private Long departmentId;

    private String departmentName;
}
