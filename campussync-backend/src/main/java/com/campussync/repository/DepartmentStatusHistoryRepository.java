package com.campussync.repository;

import com.campussync.entity.Department;
import com.campussync.entity.DepartmentStatusHistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentStatusHistoryRepository
        extends JpaRepository<DepartmentStatusHistory, Long> {

    List<DepartmentStatusHistory>
        findByDepartmentOrderByChangedAtDesc(Department department);
}
