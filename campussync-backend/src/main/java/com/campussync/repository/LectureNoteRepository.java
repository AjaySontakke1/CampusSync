package com.campussync.repository;

import com.campussync.entity.LectureNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LectureNoteRepository extends JpaRepository<LectureNote, Long> {

}
