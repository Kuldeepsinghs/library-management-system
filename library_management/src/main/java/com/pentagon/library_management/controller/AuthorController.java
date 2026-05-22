package com.pentagon.library_management.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pentagon.library_management.dto.ResponseStructure;
import com.pentagon.library_management.entity.Author;
import com.pentagon.library_management.entity.Book;
import com.pentagon.library_management.service.AuthService;
import com.pentagon.library_management.service.AuthorService;


@RestController
@RequestMapping("/authors")
public class AuthorController {

    @Autowired
    private AuthorService as;

    @Autowired
    private AuthService authService;

    
    @PostMapping
    public ResponseEntity<ResponseStructure<Author>>
            save(@RequestBody Author a, @RequestHeader(value = "X-User-Id", required = false) Integer userId) {

        if (!authService.isAdmin(userId)) {
            return forbidden();
        }
        return as.save(a);
    }

    
    @GetMapping
    public ResponseEntity<ResponseStructure<List<Author>>>
            getAllAuthor() {

        return as.getAllAuthor();
    }

    
    @GetMapping("/{id}/books")
    public ResponseEntity<ResponseStructure<List<Book>>>
            getBooks(@PathVariable int id) {

        return as.getBooks(id);
    }

    private <T> ResponseEntity<ResponseStructure<T>> forbidden() {
        ResponseStructure<T> response = new ResponseStructure<T>();
        response.setStatuscode(HttpStatus.FORBIDDEN.value());
        response.setMessage("Access denied...");
        response.setData(null);
        return new ResponseEntity<ResponseStructure<T>>(response, HttpStatus.FORBIDDEN);
    }
	 
}
