package com.campussync.entity;

import com.campussync.enums.ExamSessionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "examination_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExaminationSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "examination_session_id")
    private Long examinationSessionId;

    @Column(nullable = false, length = 150)
    private String examinationName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_id", nullable = false)
    private AcademicYear academicYear;

    @Column(nullable = false)
    private LocalDate formStartDate;

    @Column(nullable = false)
    private LocalDate formEndDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ExamSessionStatus status = ExamSessionStatus.DRAFT;
}
