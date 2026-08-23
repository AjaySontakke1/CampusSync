package com.campussync.entity;

import com.campussync.enums.ResultStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "exam_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id")
    private Long resultId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "marks_obtained")
    private Integer marksObtained;

    @Column(name = "max_marks", nullable = false)
    private Integer maxMarks;

    @Column(name = "grade", length = 5)
    private String grade;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ResultStatus status;

    @Column(name = "remarks", length = 1000)
    private String remarks;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @PrePersist
    protected void onCreate() {
        if (this.publishedAt == null) {
            this.publishedAt = LocalDateTime.now();
        }
    }
}
