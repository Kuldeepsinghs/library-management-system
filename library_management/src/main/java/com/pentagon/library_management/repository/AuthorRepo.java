package com.pentagon.library_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pentagon.library_management.entity.Author;

public interface AuthorRepo extends JpaRepository<Author,Integer>{

}
