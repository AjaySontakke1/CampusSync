package com.campussync.repository;

import com.campussync.entity.AcademicYear;
import com.campussync.entity.Assignment;
import com.campussync.entity.Course;
import com.campussync.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    List<Assignment> findByCourseAndSemesterAndAcademicYear(
            Course course,
            Semester semester,
            AcademicYear academicYear);
}
