package com.campussync.repository;

import com.campussync.entity.AcademicYear;
import com.campussync.entity.Course;
import com.campussync.entity.Semester;
import com.campussync.entity.Subject;
import com.campussync.entity.Teacher;
import com.campussync.entity.TeacherSubjectAssignment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    boolean existsBySubjectAndCourseAndSemesterAndAcademicYearAndDivision(
            Subject subject,
            Course course,
            Semester semester,
            AcademicYear academicYear,
            String division
    );

    boolean existsBySubjectAndCourseAndSemesterAndAcademicYearAndDivisionAndActiveTrue(
            Subject subject,
            Course course,
            Semester semester,
            AcademicYear academicYear,
            String division
    );

    Optional<TeacherSubjectAssignment> findByAssignmentIdAndTeacher(
            Long assignmentId,
            Teacher teacher
    );

    List<TeacherSubjectAssignment> findByTeacher(Teacher teacher);

    List<TeacherSubjectAssignment> findByActiveTrue();

    List<TeacherSubjectAssignment> findByTeacherAndActiveTrue(Teacher teacher);

    List<TeacherSubjectAssignment> findByCourseAndSemesterAndAcademicYear(
            Course course,
            Semester semester,
            AcademicYear academicYear
    );
}
