package com.pentagon.library_management.dao;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.pentagon.library_management.entity.Author;
import com.pentagon.library_management.entity.Book;
import com.pentagon.library_management.entity.BorrowRecord;
import com.pentagon.library_management.entity.BorrowStatus;
import com.pentagon.library_management.entity.Category;
import com.pentagon.library_management.entity.User;
import com.pentagon.library_management.repository.AuthorRepo;
import com.pentagon.library_management.repository.BookRepo;
import com.pentagon.library_management.repository.BorrowRecordRepo;
import com.pentagon.library_management.repository.CategoryRepo;
import com.pentagon.library_management.repository.UserRepo;

@Repository
public class BookDao {
	@Autowired
	private BookRepo book;
	
	@Autowired
	private UserRepo user;

	@Autowired
	private AuthorRepo author;

	@Autowired
	private CategoryRepo category;

	@Autowired
	private BorrowRecordRepo borrowRecord;
	
	
	public Book addBook(Book b) {
		if (b.getTotalCopies() <= 0) {
			b.setTotalCopies(1);
		}
		if (b.getAvailableCopies() <= 0 || b.getAvailableCopies() > b.getTotalCopies()) {
			b.setAvailableCopies(b.getTotalCopies());
		}

		if (b.getAuthor() != null) {
			Optional<Author> authorOpt = author.findById(b.getAuthor().getId());
			authorOpt.ifPresent(b::setAuthor);
		}

		if (b.getCategories() != null && !b.getCategories().isEmpty()) {
			List<Integer> categoryIds = b.getCategories()
					.stream()
					.map(Category::getId)
					.toList();
			b.setCategories(category.findAllById(categoryIds));
		}

		return book.save(b);
	}
	
	
	public List<Book> getAllBooks(){
		return book.findAll();
	}
	
	
	public Book getById(int id) {
		
		Optional <Book> o = book.findById(id);
		
		if(o.isPresent()) {
			return o.get();
			
		}else {
			return null;
		}
	}
	
	public BorrowRecord borrow(int b_id, int u_id) {
		
		Optional<Book> bookOpt = book.findById(b_id);
		Optional<User> userOpt = user.findById(u_id);
		
		//if book or user not found
		if(bookOpt.isEmpty() || userOpt.isEmpty()) {
			return null;
		}
		
		Book b = bookOpt.get();
		
		if(b.getAvailableCopies() <= 0) {
			return null;
		}
		
		User u = userOpt.get();

		b.setAvailableCopies(b.getAvailableCopies() - 1);
		b.setUser(u);
		book.save(b);

		BorrowRecord record = new BorrowRecord();
		record.setBook(b);
		record.setUser(u);
		record.setBorrowDate(LocalDate.now());
		record.setDueDate(LocalDate.now().plusDays(14));
		record.setFineAmount(0);
		record.setStatus(BorrowStatus.BORROWED);

		return borrowRecord.save(record);
	}
	
	
	public BorrowRecord returnBook(int id) {
		
		Optional<Book> o = book.findById(id);
		
		if(o.isPresent()) {
			Book b = o.get();
			Optional<BorrowRecord> recordOpt = borrowRecord
					.findFirstByBookIdAndStatusOrderByBorrowDateDesc(id, BorrowStatus.BORROWED);

			if (recordOpt.isEmpty()) {
				return null;
			}

			return closeBorrowRecord(recordOpt.get(), b);
			
		}else {
			return null;
		}
	}

	public BorrowRecord returnBook(int bookId, int userId) {

		Optional<Book> bookOpt = book.findById(bookId);
		Optional<BorrowRecord> recordOpt = borrowRecord
				.findFirstByBookIdAndUserIdAndStatusOrderByBorrowDateDesc(bookId, userId, BorrowStatus.BORROWED);

		if(bookOpt.isEmpty() || recordOpt.isEmpty()) {
			return null;
		}

		return closeBorrowRecord(recordOpt.get(), bookOpt.get());
	}

	private BorrowRecord closeBorrowRecord(BorrowRecord record, Book b) {
		LocalDate today = LocalDate.now();
		record.setReturnDate(today);
		record.setStatus(BorrowStatus.RETURNED);

		long lateDays = ChronoUnit.DAYS.between(record.getDueDate(), today);
		record.setFineAmount(lateDays > 0 ? lateDays * 5.0 : 0);

		if (b.getAvailableCopies() < b.getTotalCopies()) {
			b.setAvailableCopies(b.getAvailableCopies() + 1);
		}

		if (b.getAvailableCopies() == b.getTotalCopies()) {
			b.setUser(null);
		}

		book.save(b);
		return borrowRecord.save(record);
	}

	public String deleteBook(int id) {

		Optional<Book> o = book.findById(id);

		if(o.isPresent()) {
			Book b = o.get();
			book.delete(b);
			return "book deleted";
		}else {
			return null;
		}
	}
}
