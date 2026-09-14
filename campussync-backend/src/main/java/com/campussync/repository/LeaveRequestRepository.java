package com.campussync.repository;

import com.campussync.entity.LeaveRequest;
import com.campussync.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByStudentOrderByAppliedAtDesc(Student student);
}

