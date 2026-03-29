package com.test.library_loan.book.service;

import com.test.library_loan.book.model.entity.Book;

import java.util.List;
import java.util.UUID;

public interface BookDelegateService {
    Book createBook(Book book);
    List<Book> fetchAllBook();
    Book fetchDetail(UUID id, String isbn);
}
