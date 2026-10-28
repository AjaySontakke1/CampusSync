package com.campussync.dto;

import com.campussync.enums.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceResponse {

    private Long attendanceId;
    private Long studentId;
    private Long subjectId;
    private Long teacherId;
    private Long academicYearId;
    private LocalDate attendanceDate;
    private AttendanceStatus status;
    private String remarks;
}
