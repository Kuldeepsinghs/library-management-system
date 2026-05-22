package com.pentagon.library_management.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.pentagon.library_management.dao.AuthorDao;
import com.pentagon.library_management.dto.ResponseStructure;
import com.pentagon.library_management.entity.Author;
import com.pentagon.library_management.entity.Book;

@Service
public class AuthorService {

	@Autowired
	private AuthorDao ad;
	
	public ResponseEntity <ResponseStructure<Author>> save(Author a){
		Author author = ad.save(a);
		
		ResponseStructure<Author> response = new ResponseStructure<Author>();
		response.setStatuscode(HttpStatus.CREATED.value());
		response.setMessage("author added....");
		response.setData(author);
		return new ResponseEntity<ResponseStructure<Author>>(response,HttpStatus.CREATED);
	}
	
	public ResponseEntity<ResponseStructure<List<Author>>> getAllAuthor(){
		
		List<Author> a = ad.getAllauthor();
		ResponseStructure<List<Author>> response = new ResponseStructure<List<Author>>();
		response.setStatuscode(HttpStatus.OK.value());
		response.setMessage("author Fetched....");
		response.setData(a);
		return new ResponseEntity<ResponseStructure<List<Author>>>(response,HttpStatus.OK);
	}
	
	public ResponseEntity<ResponseStructure<List<Book>>> getBooks(int id){
		
		List<Book> books = ad.getBooks(id);
		if(books != null) {
			List<Book> book = ad.getBooks(id);
			ResponseStructure<List<Book>> response = new ResponseStructure<List<Book>>();
			response.setStatuscode(HttpStatus.OK.value());
			response.setMessage("Books Fetched....");
			response.setData(book);
			return new ResponseEntity<ResponseStructure<List<Book>>>(response,HttpStatus.OK);
		}
		else {
			ResponseStructure<List<Book>> response = new ResponseStructure<List<Book>>();
			response.setStatuscode(HttpStatus.NOT_FOUND.value());
			response.setMessage(" Author or book not found....");
			response.setData(books);
			return new ResponseEntity<ResponseStructure<List<Book>>>(response,HttpStatus.NOT_FOUND);

		}
		
	}
}
