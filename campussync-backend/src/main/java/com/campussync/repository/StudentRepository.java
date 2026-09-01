package com.campussync.repository;

import com.campussync.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.campussync.entity.Parent;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByParent(Parent parent);
    Optional<Student> findByStudentIdAndParent(Long studentId, Parent parent);
}
