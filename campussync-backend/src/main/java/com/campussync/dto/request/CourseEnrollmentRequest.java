package com.campussync.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseEnrollmentRequest {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Course ID is required")
    private Long courseId;

    @NotNull(message = "Semester ID is required")
    private Long semesterId;

    @NotNull(message = "Academic Year ID is required")
    private Long academicYearId;

    @NotBlank(message = "Division is required")
    @Size(max = 20, message = "Division must be 20 characters or fewer")
    private String division;
}
