package com.campussync.repository;

import com.campussync.entity.Fee;
import com.campussync.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByFeeOrderByPaymentDateDesc(Fee fee);
}
