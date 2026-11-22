package com.campussync.repository;

import com.campussync.entity.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.campussync.entity.User;
import java.util.Optional;

@Repository
public interface ParentRepository extends JpaRepository<Parent, Long> {
    Optional<Parent> findByUser(User user);

    // Find parent using associated User email
    Optional<Parent> findByUserEmail(String email);

    // Check whether a parent account already exists for this email
    boolean existsByUserEmail(String email);
}
