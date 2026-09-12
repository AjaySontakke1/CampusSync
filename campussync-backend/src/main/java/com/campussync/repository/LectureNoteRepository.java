package com.campussync.repository;

import com.campussync.entity.AcademicYear;
import com.campussync.entity.LectureNote;
import com.campussync.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LectureNoteRepository extends JpaRepository<LectureNote, Long> {

    List<LectureNote> findBySubjectSemesterAndAcademicYear(
            Semester semester,
            AcademicYear academicYear);
}
