package com.campussync.repository;

import com.campussync.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository
        extends JpaRepository<Department, Long> {

    Optional<Department> findByDepartmentCode(String departmentCode);

    boolean existsByDepartmentName(String departmentName);

    boolean existsByDepartmentCode(String departmentCode);

    List<Department> findByActive(Boolean active);

    List<Department> findByDepartmentNameContainingIgnoreCaseOrDepartmentCodeContainingIgnoreCase(
            String departmentName,
            String departmentCode
    );

    @Query("""
        SELECT d FROM Department d
        WHERE
            (:keyword IS NULL OR
                LOWER(d.departmentName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR
                LOWER(d.departmentCode) LIKE LOWER(CONCAT('%', :keyword, '%'))
            )
            AND
            (:active IS NULL OR d.active = :active)
        """)
    Page<Department> searchDepartments(
            @Param("keyword") String keyword,
            @Param("active") Boolean active,
            Pageable pageable
    );
}
