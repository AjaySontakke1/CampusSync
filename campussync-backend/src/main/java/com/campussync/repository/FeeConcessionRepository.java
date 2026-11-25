package com.campussync.repository;

import com.campussync.entity.Fee;
import com.campussync.entity.FeeConcession;
import com.campussync.enums.FeeConcessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeeConcessionRepository
        extends JpaRepository<FeeConcession, Long> {

    List<FeeConcession> findByFeeAndStatus(
            Fee fee,
            FeeConcessionStatus status
    );
}
