package com.campussync.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentProgressDTO {

    private Long assignmentId;

    private String title;

    private String description;

    private String subjectName;

    private String teacherName;

    private LocalDate assignedDate;

    private LocalDate dueDate;

    private String attachmentUrl;

    private boolean active;

    private Long submissionId;

    private LocalDateTime submissionDate;

    private String fileUrl;

    private String submissionStatus;

    private Integer marks;

    private String feedback;
}
