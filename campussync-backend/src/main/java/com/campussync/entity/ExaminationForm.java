package com.campussync.entity;

import com.campussync.enums.ExaminationFormStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "examination_forms",
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {
                "student_id",
                "examination_session_id"
            }
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExaminationForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "examination_form_id")
    private Long examinationFormId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "examination_session_id",
        nullable = false
    )
    private ExaminationSession examinationSession;

    @Column(nullable = false)
    private LocalDateTime submittedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ExaminationFormStatus status =
            ExaminationFormStatus.SUBMITTED;
}
