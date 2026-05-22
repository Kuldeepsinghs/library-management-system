package com.pentagon.library_management.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pentagon.library_management.entity.BorrowRecord;
import com.pentagon.library_management.entity.BorrowStatus;

public interface BorrowRecordRepo extends JpaRepository<BorrowRecord, Integer> {

    Optional<BorrowRecord> findFirstByBookIdAndStatusOrderByBorrowDateDesc(int bookId, BorrowStatus status);

    Optional<BorrowRecord> findFirstByBookIdAndUserIdAndStatusOrderByBorrowDateDesc(int bookId, int userId, BorrowStatus status);

    List<BorrowRecord> findByUserIdOrderByBorrowDateDesc(int userId);

    List<BorrowRecord> findByStatusOrderByBorrowDateDesc(BorrowStatus status);

    List<BorrowRecord> findByStatusAndDueDateBeforeOrderByDueDateAsc(BorrowStatus status, LocalDate date);
}
