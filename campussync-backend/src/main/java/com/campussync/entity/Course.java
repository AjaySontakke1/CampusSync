package com.campussync.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "course_name", nullable = false)
    private String courseName;

    @Column(name = "course_code", nullable = false, unique = true)
    private String courseCode;

    @Column(name = "duration_years", nullable = false)
    private int durationYears;

    @Column(name = "total_semesters", nullable = false)
    private int totalSemesters;

    @Column(name = "active", nullable = false)
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

}
