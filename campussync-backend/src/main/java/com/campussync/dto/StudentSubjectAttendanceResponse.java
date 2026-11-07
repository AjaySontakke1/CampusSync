package com.campussync.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentSubjectAttendanceResponse {

    private Long subjectId;

    private String subjectName;

    private Long totalClasses;

    private Long presentClasses;

    private Long absentClasses;

    private Double attendancePercentage;
}
