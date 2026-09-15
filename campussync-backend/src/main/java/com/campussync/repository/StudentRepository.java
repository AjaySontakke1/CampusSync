package com.campussync.repository;

import com.campussync.entity.Parent;
import com.campussync.entity.Student;
import com.campussync.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByParent(Parent parent);
    Optional<Student> findByStudentIdAndParent(Long studentId, Parent parent);
    Optional<Student> findByUser(User user);
}
