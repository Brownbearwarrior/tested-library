package com.test.library_loan.book.utils;

import com.test.library_loan.book.model.dto.BookDTO;
import com.test.library_loan.book.model.dto.request.BookRequest;
import com.test.library_loan.book.model.dto.response.BookResponse;
import com.test.library_loan.book.model.entity.Book;
import com.test.library_loan.common.exception.BusinessException;

public class BookUtils {
    private BookUtils(){}

    public static Book convertBook(BookRequest bookRequest){
        return Book.builder()
                .title(bookRequest.title().toUpperCase())
                .author(bookRequest.author().toUpperCase())
                .isbn(bookRequest.isbn().toUpperCase())
                .totalCopies(bookRequest.totalCopies())
                .availableCopies(bookRequest.totalCopies())
                .deleted(Boolean.FALSE)
                .build();
    }

    public static BookResponse convertBookResponse(Book book){
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .totalCopies(book.getTotalCopies())
                .availableCopies(book.getAvailableCopies())
                .build();
    }

    public static BookDTO convertBookDTO(Book book){
        return BookDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .availableCopies(book.getAvailableCopies())
                .build();
    }

    public static Integer setAvailableCopies(Book exist, Book newBook){
        if (exist.getTotalCopies().equals(newBook.getTotalCopies())){
            return exist.getAvailableCopies();
        } else if (exist.getTotalCopies() < newBook.getTotalCopies()) {
            return (newBook.getTotalCopies() - exist.getTotalCopies()) + exist.getAvailableCopies();
        } else {
            if (exist.getAvailableCopies().equals(0)){
                throw new BusinessException("Invalid available copies");
            }

            var result = exist.getAvailableCopies() - (exist.getTotalCopies() - newBook.getTotalCopies());

            if (result < 0){
                throw new BusinessException("Invalid available copies");
            }

            return result;
        }
    }
}
