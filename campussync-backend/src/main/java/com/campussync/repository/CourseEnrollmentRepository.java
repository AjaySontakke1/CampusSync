package com.campussync.repository;

import com.campussync.entity.CourseEnrollment;
import com.campussync.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseEnrollmentRepository
        extends JpaRepository<CourseEnrollment, Long> {

    Optional<CourseEnrollment> findByStudentAndActiveTrue(Student student);
}
