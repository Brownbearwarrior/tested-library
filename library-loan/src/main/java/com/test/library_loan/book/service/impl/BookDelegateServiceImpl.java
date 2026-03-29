package com.test.library_loan.book.service.impl;

import com.test.library_loan.book.model.entity.Book;
import com.test.library_loan.book.repository.BookRepository;
import com.test.library_loan.book.service.BookDelegateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookDelegateServiceImpl implements BookDelegateService {

    private final BookRepository bookRepository;

    @Override
    public Book createBook(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public List<Book> fetchAllBook() {
        return bookRepository.findByDeleted(Boolean.FALSE);
    }

    @Override
    public Book fetchDetail(UUID id, String isbn) {
        if (Objects.nonNull(id)){
            return bookRepository.findByIdAndDeleted(id, Boolean.FALSE);
        } else if (Objects.nonNull(isbn)) {
            return bookRepository.findByIsbnAndDeleted(isbn, Boolean.FALSE);
        }

        return null;
    }
}
