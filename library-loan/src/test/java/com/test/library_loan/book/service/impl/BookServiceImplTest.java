package com.test.library_loan.book.service.impl;

import com.test.library_loan.book.model.dto.request.BookRequest;
import com.test.library_loan.book.model.dto.response.BookResponse;
import com.test.library_loan.book.model.entity.Book;
import com.test.library_loan.book.service.BookDelegateService;
import com.test.library_loan.common.exception.BusinessException;
import com.test.library_loan.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookDelegateService bookDelegateService;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book book;
    private BookRequest bookRequest;

    @BeforeEach
    void setUp(){
        book = Book.builder()
                .id(UUID.randomUUID())
                .title("BOOK TITLE")
                .author("AUTHOR NAME")
                .isbn("1234-ASDF-QW45")
                .totalCopies(10)
                .availableCopies(10)
                .deleted(Boolean.FALSE)
                .build();

        bookRequest = BookRequest.builder()
                .title("BOOK TITLE")
                .author("AUTHOR NAME")
                .isbn("1234-ASDF-QW45")
                .totalCopies(10)
                .build();
    }

    @Test
    void createBook_SuccessResponse(){
        when(bookDelegateService.fetchDetail(null, bookRequest.isbn())).thenReturn(null);
        when(bookDelegateService.createBook(any(Book.class))).thenReturn(book);

        BookResponse result = bookService.createBook(bookRequest);

        assertNotNull(result);
        assertEquals(book.getIsbn(), result.isbn());

        verify(bookDelegateService, times(1)).fetchDetail(null, bookRequest.isbn());
        verify(bookDelegateService, times(1)).createBook(any(Book.class));
    }

    @Test
    void createBook_InvalidExist(){
        when(bookDelegateService.fetchDetail(null, bookRequest.isbn())).thenReturn(book);

        assertThrows(BusinessException.class, () -> bookService.createBook(bookRequest));

        verify(bookDelegateService, times(1)).fetchDetail(null, bookRequest.isbn());
    }

    @Test
    void fetchAllBook_SuccessResponse(){
        List<Book> books = new ArrayList<>();
        books.add(book);

        when(bookDelegateService.fetchAllBook()).thenReturn(books);

        List<BookResponse> result = bookService.fetchAllBook();

        assertEquals(1, result.size());

        verify(bookDelegateService, times(1)).fetchAllBook();
    }

    @Test
    void fetchDetailBook_SuccessResponse(){
        when(bookDelegateService.fetchDetail(book.getId(), null)).thenReturn(book);

        BookResponse result = bookService.fetchDetailBook(book.getId());

        assertNotNull(result);
        assertEquals(book.getId(), result.id());

        verify(bookDelegateService, times(1)).fetchDetail(book.getId(), null);
    }

    @Test
    void fetchDetailBook_NotFound(){
        when(bookDelegateService.fetchDetail(book.getId(), null)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> bookService.fetchDetailBook(book.getId()));

        verify(bookDelegateService, times(1)).fetchDetail(book.getId(), null);
    }

    @Test
    void updateBook_SuccessResponse(){
        when(bookDelegateService.fetchDetail(null, bookRequest.isbn())).thenReturn(book);
        when(bookDelegateService.fetchDetail(book.getId(), null)).thenReturn(book);
        when(bookDelegateService.createBook(any(Book.class))).thenReturn(book);

        BookResponse result = bookService.updateBook(book.getId(), bookRequest);

        assertNotNull(result);
        assertEquals(book.getIsbn(), result.isbn());
        assertEquals(book.getId(), result.id());

        verify(bookDelegateService, times(1)).fetchDetail(null, bookRequest.isbn());
        verify(bookDelegateService, times(1)).fetchDetail(book.getId(), null);
        verify(bookDelegateService, times(1)).createBook(any(Book.class));
    }

    @Test
    void updateBook_InvalidAlreadyExist(){
        when(bookDelegateService.fetchDetail(null, bookRequest.isbn())).thenReturn(book);

        assertThrows(BusinessException.class, () -> bookService.updateBook(UUID.randomUUID(), bookRequest));

        verify(bookDelegateService, times(1)).fetchDetail(null, bookRequest.isbn());
    }

    @Test
    void updateBook_NotFound(){
        when(bookDelegateService.fetchDetail(null, bookRequest.isbn())).thenReturn(null);
        when(bookDelegateService.fetchDetail(book.getId(), null)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> bookService.updateBook(book.getId(), bookRequest));

        verify(bookDelegateService, times(1)).fetchDetail(null, bookRequest.isbn());
        verify(bookDelegateService, times(1)).fetchDetail(book.getId(), null);
    }

    @Test
    void deleteBook_SuccessResponse(){
        when(bookDelegateService.fetchDetail(book.getId(), null)).thenReturn(book);
        when(bookDelegateService.createBook(any(Book.class))).thenReturn(book);

        String result = bookService.deleteBook(book.getId());

        assertNotNull(result);

        verify(bookDelegateService, times(1)).fetchDetail(book.getId(), null);
        verify(bookDelegateService, times(1)).createBook(any(Book.class));
    }

    @Test
    void deleteBook_NotFound(){
        when(bookDelegateService.fetchDetail(book.getId(), null)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> bookService.deleteBook(book.getId()));

        verify(bookDelegateService, times(1)).fetchDetail(book.getId(), null);
    }

    @Test
    void deleteBook_InvalidBorrowed(){
        Book invalidBook = Book.builder()
                .id(UUID.randomUUID())
                .title("BOOK TITLE")
                .author("AUTHOR NAME")
                .isbn("1234-ASDF-QW45")
                .totalCopies(10)
                .availableCopies(9)
                .deleted(Boolean.FALSE)
                .build();

        when(bookDelegateService.fetchDetail(invalidBook.getId(), null)).thenReturn(invalidBook);

        assertThrows(BusinessException.class, () -> bookService.deleteBook(invalidBook.getId()));

        verify(bookDelegateService, times(1)).fetchDetail(invalidBook.getId(), null);
    }
}
