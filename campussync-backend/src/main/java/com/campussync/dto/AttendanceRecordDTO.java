package com.campussync.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceRecordDTO {
    private Long attendanceId;
    private String subjectName;
    private LocalDate attendanceDate;
    private String status;
    private String remarks;
}
