package com.campussync.repository;

import com.campussync.entity.SemesterOffering;
import com.campussync.entity.Semester;
import com.campussync.entity.AcademicYear;
import com.campussync.enums.SemesterStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SemesterOfferingRepository
        extends JpaRepository<SemesterOffering, Long> {

    // Check whether an offering already exists
    boolean existsBySemesterAndAcademicYear(
            Semester semester,
            AcademicYear academicYear
    );

    // Find offering by semester and academic year
    Optional<SemesterOffering> findBySemesterAndAcademicYear(
            Semester semester,
            AcademicYear academicYear
    );

    // Get all offerings for a semester
    List<SemesterOffering> findBySemester(Semester semester);

    // Get all offerings for an academic year
    List<SemesterOffering> findByAcademicYear(
            AcademicYear academicYear
    );

    // Get offerings by status
    List<SemesterOffering> findByStatus(
            SemesterStatus status
    );

    // Get offerings by academic year and status
    List<SemesterOffering> findByAcademicYearAndStatus(
            AcademicYear academicYear,
            SemesterStatus status
    );
}
