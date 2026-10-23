package com.campussync.repository;

import com.campussync.entity.AcademicYear;
import com.campussync.entity.Course;
import com.campussync.entity.Semester;
import com.campussync.entity.Subject;
import com.campussync.entity.Teacher;
import com.campussync.entity.TeacherSubjectAssignment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherSubjectAssignmentRepository
        extends JpaRepository<TeacherSubjectAssignment, Long> {

    boolean existsByTeacherAndSubjectAndCourseAndSemesterAndAcademicYear(
            Teacher teacher,
            Subject subject,
            Course course,
            Semester semester,
            AcademicYear academicYear
    );
}

