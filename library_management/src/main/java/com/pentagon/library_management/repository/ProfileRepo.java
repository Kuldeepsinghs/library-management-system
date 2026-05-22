package com.pentagon.library_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pentagon.library_management.entity.Profile;

public interface ProfileRepo extends JpaRepository<Profile,String> {

}
