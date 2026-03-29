package com.test.library_loan.book.service;

import com.test.library_loan.book.model.dto.request.BookRequest;
import com.test.library_loan.book.model.dto.response.BookResponse;

import java.util.List;
import java.util.UUID;

public interface BookService {
    BookResponse createBook(BookRequest bookRequest);
    List<BookResponse> fetchAllBook();
    BookResponse fetchDetailBook(UUID id);
    BookResponse updateBook(UUID id, BookRequest bookRequest);
    String deleteBook(UUID id);
}
