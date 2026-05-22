package com.pentagon.library_management.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.pentagon.library_management.dao.BookDao;
import com.pentagon.library_management.dto.ResponseStructure;
import com.pentagon.library_management.entity.Book;
import com.pentagon.library_management.entity.BorrowRecord;
import com.pentagon.library_management.entity.BorrowStatus;
import com.pentagon.library_management.repository.BorrowRecordRepo;

@Service
public class BookService {
	
	@Autowired
	private BookDao bd;

    @Autowired
    private BorrowRecordRepo borrowRecordRepo;
	
	
    public ResponseEntity<ResponseStructure<Book>> addBook(Book b) {

        Book book = bd.addBook(b);

        ResponseStructure<Book> response = new ResponseStructure<Book>();

        response.setStatuscode(HttpStatus.CREATED.value());
        response.setMessage("Book Added Successfully...");
        response.setData(book);

        return new ResponseEntity<ResponseStructure<Book>>(response, HttpStatus.CREATED);
    }
    
    public ResponseEntity<ResponseStructure<List<Book>>> getAllBooks() {

        List<Book> books = bd.getAllBooks();

        ResponseStructure<List<Book>> response = new ResponseStructure<List<Book>>();

        response.setStatuscode(HttpStatus.OK.value());
        response.setMessage("Books Fetched Successfully...");
        response.setData(books);

        return new ResponseEntity<ResponseStructure<List<Book>>>(response, HttpStatus.OK);
    }
    
    
    
    public ResponseEntity<ResponseStructure<Book>> getById(int id) {

        Book b = bd.getById(id);

        ResponseStructure<Book> response = new ResponseStructure<Book>();

        if (b != null) {

            response.setStatuscode(HttpStatus.OK.value());
            response.setMessage("Book Found...");
            response.setData(b);

            return new ResponseEntity<ResponseStructure<Book>>(response, HttpStatus.OK);

        } else {

            response.setStatuscode(HttpStatus.NOT_FOUND.value());
            response.setMessage("Book Not Found...");
            response.setData(null);

            return new ResponseEntity<ResponseStructure<Book>>(response, HttpStatus.NOT_FOUND);
        }
    }
    
    
    public ResponseEntity<ResponseStructure<BorrowRecord>> borrow(int b_id, int u_id) {

        BorrowRecord record = bd.borrow(b_id, u_id);

        ResponseStructure<BorrowRecord> response = new ResponseStructure<BorrowRecord>();

        if (record != null) {

            response.setStatuscode(HttpStatus.OK.value());
            response.setMessage("Book Borrowed Successfully. Due date is " + record.getDueDate() + "...");
            response.setData(record);

            return new ResponseEntity<ResponseStructure<BorrowRecord>>(response, HttpStatus.OK);

        } else {

            response.setStatuscode(HttpStatus.NOT_FOUND.value());
            response.setMessage("Book/User Not Found OR No Copies Available...");
            response.setData(null);

            return new ResponseEntity<ResponseStructure<BorrowRecord>>(response, HttpStatus.NOT_FOUND);
        }
    }
    
    
    public ResponseEntity<ResponseStructure<BorrowRecord>> returnBook(int id) {

        BorrowRecord record = bd.returnBook(id);

        ResponseStructure<BorrowRecord> response = new ResponseStructure<BorrowRecord>();

        if (record != null) {

            response.setStatuscode(HttpStatus.OK.value());
            response.setMessage("Book Returned Successfully. Fine: " + record.getFineAmount() + "...");
            response.setData(record);

            return new ResponseEntity<ResponseStructure<BorrowRecord>>(response, HttpStatus.OK);

        } else {

            response.setStatuscode(HttpStatus.NOT_FOUND.value());
            response.setMessage("Book Not Found OR No Active Borrow Record...");
            response.setData(null);

            return new ResponseEntity<ResponseStructure<BorrowRecord>>(response, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<ResponseStructure<BorrowRecord>> returnBook(int bookId, int userId) {

        BorrowRecord record = bd.returnBook(bookId, userId);

        ResponseStructure<BorrowRecord> response = new ResponseStructure<BorrowRecord>();

        if (record != null) {
            response.setStatuscode(HttpStatus.OK.value());
            response.setMessage("Book Returned Successfully. Fine: " + record.getFineAmount() + "...");
            response.setData(record);
            return new ResponseEntity<ResponseStructure<BorrowRecord>>(response, HttpStatus.OK);
        }

        response.setStatuscode(HttpStatus.NOT_FOUND.value());
        response.setMessage("Active borrow record not found...");
        response.setData(null);
        return new ResponseEntity<ResponseStructure<BorrowRecord>>(response, HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<ResponseStructure<List<BorrowRecord>>> getBorrowRecords() {
        List<BorrowRecord> records = borrowRecordRepo.findAll();
        ResponseStructure<List<BorrowRecord>> response = new ResponseStructure<List<BorrowRecord>>();
        response.setStatuscode(HttpStatus.OK.value());
        response.setMessage("Borrow Records Fetched Successfully...");
        response.setData(records);
        return new ResponseEntity<ResponseStructure<List<BorrowRecord>>>(response, HttpStatus.OK);
    }

    public ResponseEntity<ResponseStructure<List<BorrowRecord>>> getBorrowHistory(int userId) {
        List<BorrowRecord> records = borrowRecordRepo.findByUserIdOrderByBorrowDateDesc(userId);
        ResponseStructure<List<BorrowRecord>> response = new ResponseStructure<List<BorrowRecord>>();
        response.setStatuscode(HttpStatus.OK.value());
        response.setMessage("Borrow History Fetched Successfully...");
        response.setData(records);
        return new ResponseEntity<ResponseStructure<List<BorrowRecord>>>(response, HttpStatus.OK);
    }

    public ResponseEntity<ResponseStructure<List<BorrowRecord>>> getOverdueBooks() {
        List<BorrowRecord> records = borrowRecordRepo
                .findByStatusAndDueDateBeforeOrderByDueDateAsc(BorrowStatus.BORROWED, LocalDate.now());
        ResponseStructure<List<BorrowRecord>> response = new ResponseStructure<List<BorrowRecord>>();
        response.setStatuscode(HttpStatus.OK.value());
        response.setMessage("Overdue Books Fetched Successfully...");
        response.setData(records);
        return new ResponseEntity<ResponseStructure<List<BorrowRecord>>>(response, HttpStatus.OK);
    }

    public ResponseEntity<ResponseStructure<String>> deleteBook(int id) {

        String result = bd.deleteBook(id);

        ResponseStructure<String> response = new ResponseStructure<String>();

        if (result != null) {

            response.setStatuscode(HttpStatus.OK.value());
            response.setMessage("Book Deleted Successfully...");
            response.setData(result);

            return new ResponseEntity<ResponseStructure<String>>(response, HttpStatus.OK);

        } else {

            response.setStatuscode(HttpStatus.NOT_FOUND.value());
            response.setMessage("Book Not Found...");
            response.setData(null);

            return new ResponseEntity<ResponseStructure<String>>(response, HttpStatus.NOT_FOUND);
        }
    }
}
