package com.campussync.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentResponseDTO {

    private Long assignmentId;

    private String title;

    private String description;

    private String subjectName;

    private String teacherName;

    private LocalDate assignedDate;

    private LocalDate dueDate;

    private String attachmentUrl;

    private boolean active;
}
