package com.campussync.repository;

import com.campussync.entity.AcademicYear;
import com.campussync.entity.Course;
import com.campussync.entity.CourseEnrollment;
import com.campussync.entity.Semester;
import com.campussync.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseEnrollmentRepository
        extends JpaRepository<CourseEnrollment, Long> {

    Optional<CourseEnrollment> findByStudentAndActiveTrue(Student student);

    List<CourseEnrollment>
    findByCourseAndSemesterAndAcademicYearAndDivisionAndActiveTrue(
            Course course,
            Semester semester,
            AcademicYear academicYear,
            String division
    );
}
