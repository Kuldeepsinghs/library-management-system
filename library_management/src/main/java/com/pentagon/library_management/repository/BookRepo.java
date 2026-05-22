package com.pentagon.library_management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pentagon.library_management.entity.Book;

public interface BookRepo extends JpaRepository<Book,Integer>{
	List<Book> findByAuthorId(int authorId);
}
