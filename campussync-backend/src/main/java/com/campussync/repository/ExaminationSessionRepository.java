package com.campussync.repository;

import com.campussync.entity.AcademicYear;
import com.campussync.entity.ExaminationSession;
import com.campussync.entity.Semester;
import com.campussync.enums.ExamSessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExaminationSessionRepository
        extends JpaRepository<ExaminationSession, Long> {

    Optional<ExaminationSession> findByExaminationSessionIdAndStatus(
            Long examinationSessionId,
            ExamSessionStatus status
    );

    List<ExaminationSession> findByStatusOrderByFormStartDateAsc(
            ExamSessionStatus status
    );

    boolean existsByExaminationNameAndSemesterAndAcademicYear(
            String examinationName,
            Semester semester,
            AcademicYear academicYear
    );
}
