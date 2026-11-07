package com.campussync.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceSummaryResponse {

    private Long subjectId;

    private String subjectName;

    private Long totalStudents;

    private Long presentStudents;

    private Long absentStudents;

    private Double attendancePercentage;
}
