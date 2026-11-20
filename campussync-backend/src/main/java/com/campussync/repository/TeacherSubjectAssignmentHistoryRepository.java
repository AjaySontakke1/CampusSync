package com.campussync.repository;

import com.campussync.entity.TeacherSubjectAssignment;
import com.campussync.entity.TeacherSubjectAssignmentHistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherSubjectAssignmentHistoryRepository
        extends JpaRepository<TeacherSubjectAssignmentHistory, Long> {

    List<TeacherSubjectAssignmentHistory>
    findByAssignmentOrderByChangedAtDesc(
            TeacherSubjectAssignment assignment
    );
}
