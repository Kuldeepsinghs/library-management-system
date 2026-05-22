package com.pentagon.library_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pentagon.library_management.entity.Category;

public interface CategoryRepo extends JpaRepository<Category,Integer>{

}
