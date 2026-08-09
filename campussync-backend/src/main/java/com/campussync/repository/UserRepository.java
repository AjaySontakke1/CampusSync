package com.campussync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.campussync.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}
