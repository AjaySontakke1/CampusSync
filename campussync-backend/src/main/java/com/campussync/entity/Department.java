package com.campussync.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "departments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "department_id")
    private Long departmentId;

    @Column(nullable = false, unique = true)
    private String departmentName;

    @Column(nullable = false, unique = true)
    private String departmentCode;

    private String hodName;

}
