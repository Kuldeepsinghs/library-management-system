package com.pentagon.library_management.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.pentagon.library_management.entity.Author;
import com.pentagon.library_management.entity.Book;
import com.pentagon.library_management.repository.AuthorRepo;
import com.pentagon.library_management.repository.BookRepo;

@Repository
public class AuthorDao {
	@Autowired
	private AuthorRepo author;

	@Autowired
	private BookRepo book;
	
	public Author save(Author a) {
		return author.save(a);
	}
	
	public List<Author> getAllauthor(){
		return author.findAll();
	}
	
	public List<Book> getBooks(int id){
		Optional<Author> o = author.findById(id);
		if(o.isPresent()) {
			return book.findByAuthorId(id);
		}else {
			return null;
		}
	}
}
