package com.test.library_loan.book.repository;

import com.test.library_loan.book.model.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {
    Book findByIsbnAndDeleted(String isbn, Boolean deleted);
    Book findByIdAndDeleted(UUID id, Boolean deleted);
    List<Book> findByDeleted(Boolean deleted);
}
