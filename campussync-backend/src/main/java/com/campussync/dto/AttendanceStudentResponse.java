package com.campussync.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceStudentResponse {

    private Long studentId;

    private String studentName;

    private String rollNumber;

    private String registrationNumber;
}
