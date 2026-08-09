package com.campussync.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "semesters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Semester {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "semester_id")
    private Long semesterId;

    @Column(name = "semester_number", nullable = false)
    private int semesterNumber;

    @Column(name = "semester_name", nullable = false)
    private String semesterName;

    @Column(name = "active", nullable = false)
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

}
