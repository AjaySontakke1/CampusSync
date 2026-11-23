package com.campussync.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "fee_structures")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeeStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fee_structure_id")
    private Long feeStructureId;

    // Course for which this fee applies
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    // Optional: semester-wise fee
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id")
    private Semester semester;

    // Academic year
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_id", nullable = false)
    private AcademicYear academicYear;

    // Total configured fee
    @Column(name = "amount", nullable = false,
            precision = 10, scale = 2)
    private BigDecimal amount;

    // Fee payment deadline
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    // Whether Admin can currently use this structure
    @Builder.Default
    @Column(name = "active", nullable = false)
    private boolean active = true;
}
