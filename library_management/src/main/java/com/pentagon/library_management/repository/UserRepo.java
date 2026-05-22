package com.pentagon.library_management.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pentagon.library_management.entity.Role;
import com.pentagon.library_management.entity.User;

public interface UserRepo extends JpaRepository<User,Integer> {

    Optional<User> findByProfileEmail(String email);

    boolean existsByRole(Role role);
}
