package com.test.library_loan.book.controller;

import com.test.library_loan.book.model.dto.request.BookRequest;
import com.test.library_loan.book.model.dto.response.BookResponse;
import com.test.library_loan.book.service.BookService;
import com.test.library_loan.common.model.Response;
import com.test.library_loan.common.utils.ResponseUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/book")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping
    ResponseEntity<Response<BookResponse>> createBook(@Valid @RequestBody BookRequest bookRequest){
        return ResponseEntity.ok(
                ResponseUtils.toResponse("Success",
                        bookService.createBook(bookRequest)));
    }

    @GetMapping
    ResponseEntity<Response<List<BookResponse>>> fetchBookList(){
        return ResponseEntity.ok(
                ResponseUtils.toResponse("Success",
                        bookService.fetchAllBook()));
    }

    @GetMapping("/{id}")
    ResponseEntity<Response<BookResponse>> fetchBookDetail(@PathVariable UUID id){
        return ResponseEntity.ok(
                ResponseUtils.toResponse("Success",
                        bookService.fetchDetailBook(id)));
    }

    @PutMapping("/{id}")
    ResponseEntity<Response<BookResponse>> updateBook(@PathVariable UUID id,
                                                      @Valid @RequestBody BookRequest bookRequest){
        return ResponseEntity.ok(
                ResponseUtils.toResponse("Success",
                        bookService.updateBook(id, bookRequest)));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Response<String>> deleteBook(@PathVariable UUID id){
        return ResponseEntity.ok(
                ResponseUtils.toResponse("Success",
                        bookService.deleteBook(id)));
    }
}
