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
public class ExamResultResponseDTO {

    private Long resultId;

    private String examTitle;

    private String examType;

    private String subjectName;

    private LocalDate examDate;

    private Integer marksObtained;

    private Integer maxMarks;

    private String grade;

    private String status;

    private String remarks;

    private LocalDateTime publishedAt;
}
