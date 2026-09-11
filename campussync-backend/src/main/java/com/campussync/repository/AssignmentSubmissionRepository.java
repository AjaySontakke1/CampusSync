package com.campussync.repository;

import com.campussync.entity.AssignmentSubmission;
import com.campussync.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentSubmissionRepository
        extends JpaRepository<AssignmentSubmission, Long> {

    List<AssignmentSubmission> findByStudent(Student student);

    @Query("SELECT s FROM AssignmentSubmission s WHERE s.assignment.assignmentId = :assignmentId AND s.student = :student")
    Optional<AssignmentSubmission> findByAssignmentIdAndStudent(
            @Param("assignmentId") Long assignmentId,
            @Param("student") Student student);
}
