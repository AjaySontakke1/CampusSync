package com.campussync.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
