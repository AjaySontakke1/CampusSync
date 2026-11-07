package com.campussync.repository;

import com.campussync.entity.Attendance;
import com.campussync.entity.Student;
import com.campussync.entity.Subject;
import com.campussync.entity.Teacher;
import com.campussync.entity.AcademicYear;
import com.campussync.enums.AttendanceStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepository
        extends JpaRepository<Attendance, Long> {

    List<Attendance> findByStudent(Student student);

    List<Attendance> findByStudentAndAcademicYear(
            Student student,
            AcademicYear academicYear);

    List<Attendance> findByTeacherAndAttendanceDate(
            Teacher teacher,
            LocalDate attendanceDate);

    List<Attendance> findByTeacherAndSubjectAndAttendanceDate(
            Teacher teacher,
            Subject subject,
            LocalDate attendanceDate);

    long countByTeacherAndSubjectAndAttendanceDate(
            Teacher teacher,
            Subject subject,
            LocalDate attendanceDate);

    long countByTeacherAndSubjectAndAttendanceDateAndStatus(
            Teacher teacher,
            Subject subject,
            LocalDate attendanceDate,
            AttendanceStatus status);

    boolean existsByStudentAndSubjectAndAcademicYearAndAttendanceDate(
            Student student,
            Subject subject,
            AcademicYear academicYear,
            LocalDate attendanceDate);
}

