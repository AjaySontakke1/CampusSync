package com.campussync.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LectureNoteResponseDTO {

    private Long noteId;

    private String title;

    private String description;

    private String subjectName;

    private String teacherName;

    private String fileName;

    private String fileType;

    private Long fileSize;

    private String fileUrl;

    private LocalDateTime uploadedAt;

    private boolean active;
}
