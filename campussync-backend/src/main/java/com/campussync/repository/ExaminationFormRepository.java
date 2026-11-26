package com.campussync.repository;

import com.campussync.entity.ExaminationForm;
import com.campussync.entity.ExaminationSession;
import com.campussync.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExaminationFormRepository
        extends JpaRepository<ExaminationForm, Long> {

    boolean existsByStudentAndExaminationSession(
            Student student,
            ExaminationSession examinationSession
    );

    List<ExaminationForm> findByStudentOrderBySubmittedAtDesc(
            Student student
    );
}
