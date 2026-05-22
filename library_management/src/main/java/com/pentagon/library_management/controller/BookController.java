package com.pentagon.library_management.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pentagon.library_management.dto.ResponseStructure;
import com.pentagon.library_management.entity.Book;
import com.pentagon.library_management.entity.BorrowRecord;
import com.pentagon.library_management.service.AuthService;
import com.pentagon.library_management.service.BookService;

@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bs;

    @Autowired
    private AuthService authService;

    
    @PostMapping
    public ResponseEntity<ResponseStructure<Book>>
            addBook(@RequestBody Book b, @RequestHeader(value = "X-User-Id", required = false) Integer userId) {

        if (!authService.isAdmin(userId)) {
            return forbidden();
        }
        return bs.addBook(b);
    }

    
    @GetMapping
    public ResponseEntity<ResponseStructure<List<Book>>>
            getAllBooks() {

        return bs.getAllBooks();
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<ResponseStructure<Book>>
            getById(@PathVariable int id) {

        return bs.getById(id);
    }

    
    
    @PutMapping("/{b_id}/borrow/{u_id}")
    public ResponseEntity<ResponseStructure<BorrowRecord>>
            borrow(@PathVariable int b_id, @PathVariable int u_id,
                    @RequestHeader(value = "X-User-Id", required = false) Integer userId) {

        if (!authService.canAccessUser(userId, u_id)) {
            return forbidden();
        }
        return bs.borrow(b_id, u_id);
    }

    
    @PutMapping("/{id}/return")
    public ResponseEntity<ResponseStructure<BorrowRecord>>
            returnBook(@PathVariable int id, @RequestHeader(value = "X-User-Id", required = false) Integer userId) {

        if (!authService.isAdmin(userId)) {
            return forbidden();
        }
        return bs.returnBook(id);
    }

    @PutMapping("/{bookId}/return/{userId}")
    public ResponseEntity<ResponseStructure<BorrowRecord>>
            returnBookForUser(@PathVariable int bookId, @PathVariable int userId,
                    @RequestHeader(value = "X-User-Id", required = false) Integer loggedInUserId) {

        if (!authService.canAccessUser(loggedInUserId, userId)) {
            return forbidden();
        }
        return bs.returnBook(bookId, userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseStructure<String>>
            deleteBook(@PathVariable int id, @RequestHeader(value = "X-User-Id", required = false) Integer userId) {

        if (!authService.isAdmin(userId)) {
            return forbidden();
        }
        return bs.deleteBook(id);
    }

    @GetMapping("/borrow-records")
    public ResponseEntity<ResponseStructure<List<BorrowRecord>>>
            getBorrowRecords(@RequestHeader(value = "X-User-Id", required = false) Integer userId) {

        if (!authService.isAdmin(userId)) {
            return forbidden();
        }
        return bs.getBorrowRecords();
    }

    @GetMapping("/overdue")
    public ResponseEntity<ResponseStructure<List<BorrowRecord>>>
            getOverdueBooks(@RequestHeader(value = "X-User-Id", required = false) Integer userId) {

        if (!authService.isAdmin(userId)) {
            return forbidden();
        }
        return bs.getOverdueBooks();
    }

    @GetMapping("/users/{userId}/history")
    public ResponseEntity<ResponseStructure<List<BorrowRecord>>>
            getBorrowHistory(@PathVariable int userId,
                    @RequestHeader(value = "X-User-Id", required = false) Integer loggedInUserId) {

        if (!authService.canAccessUser(loggedInUserId, userId)) {
            return forbidden();
        }
        return bs.getBorrowHistory(userId);
    }

    private <T> ResponseEntity<ResponseStructure<T>> forbidden() {
        ResponseStructure<T> response = new ResponseStructure<T>();
        response.setStatuscode(HttpStatus.FORBIDDEN.value());
        response.setMessage("Access denied...");
        response.setData(null);
        return new ResponseEntity<ResponseStructure<T>>(response, HttpStatus.FORBIDDEN);
    }
}
