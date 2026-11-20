package com.campussync.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherSubjectAssignmentRequest {

    @NotNull(message = "Teacher ID is required")
    @Positive
    private Long teacherId;

    @NotNull(message = "Subject ID is required")
    @Positive
    private Long subjectId;

    @NotNull(message = "Course ID is required")
    @Positive
    private Long courseId;

    @NotNull(message = "Semester ID is required")
    @Positive
    private Long semesterId;

    @NotNull(message = "Academic year ID is required")
    @Positive
    private Long academicYearId;

    @NotBlank(message = "Division is required")
    private String division;
}
