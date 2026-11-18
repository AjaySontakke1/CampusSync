package com.campussync.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "semester_subject_assignments",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_offering_subject",
            columnNames = {"semester_offering_id", "subject_id"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SemesterSubjectAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assignment_id")
    private Long assignmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_offering_id", nullable = false)
    private SemesterOffering semesterOffering;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;
}
