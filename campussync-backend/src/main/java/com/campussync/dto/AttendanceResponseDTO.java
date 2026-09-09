package com.campussync.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceResponseDTO {
    private int totalClasses;
    private int presentCount;
    private int absentCount;
    private double attendancePercentage;
    private List<AttendanceRecordDTO> records;
}
