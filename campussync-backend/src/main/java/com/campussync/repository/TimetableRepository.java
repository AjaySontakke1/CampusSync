package com.campussync.repository;

import com.campussync.entity.Course;
import com.campussync.entity.Semester;
import com.campussync.entity.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimetableRepository extends JpaRepository<Timetable, Long> {

    List<Timetable> findByCourseAndSemesterOrderByDayOfWeekAscStartTimeAsc(
            Course course,
            Semester semester);
}
