package com.campussync.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseRequest {

    @NotBlank(message = "Course name is required")
    @Size(max = 100, message = "Course name cannot exceed 100 characters")
    private String courseName;

    @NotBlank(message = "Course code is required")
    @Size(max = 20, message = "Course code cannot exceed 20 characters")
    private String courseCode;

    @Min(value = 1, message = "Duration must be at least 1 year")
    @Max(value = 10, message = "Duration cannot exceed 10 years")
    private int durationYears;

    @Min(value = 1, message = "Total semesters must be at least 1")
    @Max(value = 20, message = "Total semesters cannot exceed 20")
    private int totalSemesters;

    @NotNull(message = "Department ID is required")
    @Positive(message = "Department ID must be positive")
    private Long departmentId;
}
