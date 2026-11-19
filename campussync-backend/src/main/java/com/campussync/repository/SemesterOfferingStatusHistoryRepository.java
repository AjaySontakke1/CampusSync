package com.campussync.repository;

import com.campussync.entity.SemesterOffering;
import com.campussync.entity.SemesterOfferingStatusHistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SemesterOfferingStatusHistoryRepository
        extends JpaRepository<SemesterOfferingStatusHistory, Long> {

    // Find all history records for one offering
    List<SemesterOfferingStatusHistory>
    findBySemesterOfferingOrderByChangedAtDesc(
            SemesterOffering semesterOffering
    );
}
