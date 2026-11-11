package com.campussync.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceStudentResponse {

    private Long studentId;
    private String rollNumber;
    private String registrationNumber;
    private String firstName;
    private String lastName;
}
