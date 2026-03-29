package com.test.library_loan.book.service.impl;

import com.test.library_loan.book.model.dto.request.BookRequest;
import com.test.library_loan.book.model.dto.response.BookResponse;
import com.test.library_loan.book.model.entity.Book;
import com.test.library_loan.book.service.BookDelegateService;
import com.test.library_loan.book.service.BookService;
import com.test.library_loan.book.utils.BookUtils;
import com.test.library_loan.common.exception.BusinessException;
import com.test.library_loan.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookDelegateService bookDelegateService;

    @Override
    @Transactional
    public BookResponse createBook(BookRequest bookRequest) {
        Book book = BookUtils.convertBook(bookRequest);

        var exist = this.fetchBook(null, book.getIsbn());

        if (Objects.nonNull(exist)){
            throw new BusinessException("Book already exist");
        }

        var saved = this.saveBook(book);

        return BookUtils.convertBookResponse(saved);
    }

    @Override
    public List<BookResponse> fetchAllBook() {
        var books = this.fetchBooks();

        return books.stream().map(BookUtils::convertBookResponse).toList();
    }

    @Override
    public BookResponse fetchDetailBook(UUID id) {
        var book = this.fetchExistBook(id);

        return BookUtils.convertBookResponse(book);
    }

    @Override
    @Transactional
    public BookResponse updateBook(UUID id, BookRequest bookRequest) {
        Book request = BookUtils.convertBook(bookRequest);

        var check = this.fetchBook(null, request.getIsbn());

        if (Objects.nonNull(check) && check.getId() != id){
            throw new BusinessException("Book already exist");
        }

        var exist = this.fetchExistBook(id);

        exist.setTitle(request.getTitle());
        exist.setAuthor(request.getAuthor());
        exist.setIsbn(request.getIsbn());
        exist.setTotalCopies(request.getTotalCopies());
        exist.setAvailableCopies(BookUtils.setAvailableCopies(exist, request));

        var updated = this.saveBook(exist);

        return BookUtils.convertBookResponse(updated);
    }

    @Override
    @Transactional
    public String deleteBook(UUID id) {
        var exist = this.fetchExistBook(id);

        exist.setDeleted(Boolean.TRUE);

        this.saveBook(exist);

        return id.toString() + " Deleted Successfully";
    }

    private Book fetchExistBook(UUID id){
        var book = this.fetchBook(id, null);

        if (Objects.isNull(book)){
            throw new ResourceNotFoundException("Book not found");
        }

        return book;
    }

    private Book saveBook(Book book){
        return bookDelegateService.createBook(book);
    }

    private List<Book> fetchBooks(){
        return bookDelegateService.fetchAllBook();
    }

    private Book fetchBook(UUID id, String isbn){
        return bookDelegateService.fetchDetail(id, isbn);
    }
}
