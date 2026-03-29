package com.test.library_loan.book.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.library_loan.book.model.dto.request.BookRequest;
import com.test.library_loan.book.model.dto.response.BookResponse;
import com.test.library_loan.book.service.BookService;
import com.test.library_loan.common.config.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@Import({SecurityConfig.class})
class BookControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    BookService bookService;

    private ObjectMapper objectMapper;
    private BookRequest bookRequest;
    private BookResponse bookResponse;

    @BeforeEach
    void setUp(){
        objectMapper = new ObjectMapper();

        bookRequest = BookRequest.builder()
                .title("BOOK TITLE")
                .author("AUTHOR NAME")
                .isbn("1234-ASDF-QW45")
                .totalCopies(10)
                .build();

        bookResponse = BookResponse.builder()
                .id(UUID.randomUUID())
                .title("BOOK TITLE")
                .author("AUTHOR NAME")
                .isbn("1234-ASDF-QW45")
                .totalCopies(10)
                .availableCopies(10)
                .build();
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "LIBRARIAN"})
    void createBook_ShouldReturnSuccess() throws Exception{
        when(bookService.createBook(any(BookRequest.class))).thenReturn(bookResponse);

        mockMvc.perform(post("/api/book")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.isbn").value("1234-ASDF-QW45"));

        verify(bookService).createBook(any(BookRequest.class));
    }

    @Test
    void createBook_ShouldReturnFailed() throws Exception{
        mockMvc.perform(post("/api/book")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "LIBRARIAN", "MEMBER"})
    void fetchBookList_ShouldReturnSuccess() throws Exception{
        List<BookResponse> bookResponses = new ArrayList<>();
        bookResponses.add(bookResponse);

        when(bookService.fetchAllBook()).thenReturn(bookResponses);

        mockMvc.perform(get("/api/book")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].isbn").value("1234-ASDF-QW45"));

        verify(bookService).fetchAllBook();
    }

    @Test
    void fetchBookList_ShouldReturnFailed() throws Exception{
        mockMvc.perform(get("/api/book")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "LIBRARIAN", "MEMBER"})
    void fetchBookDetail_ShouldReturnSuccess() throws Exception{
        when(bookService.fetchDetailBook(any(UUID.class))).thenReturn(bookResponse);

        mockMvc.perform(get("/api/book/"+bookResponse.id().toString())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.isbn").value("1234-ASDF-QW45"));

        verify(bookService).fetchDetailBook(any(UUID.class));
    }

    @Test
    void fetchBookDetail_ShouldReturnFailed() throws Exception{
        mockMvc.perform(get("/api/book/"+bookResponse.id().toString())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "LIBRARIAN"})
    void updateBook_ShouldReturnSuccess() throws Exception{
        when(bookService.updateBook(any(UUID.class), any(BookRequest.class))).thenReturn(bookResponse);

        mockMvc.perform(put("/api/book/"+bookResponse.id().toString())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.isbn").value("1234-ASDF-QW45"));

        verify(bookService).updateBook(any(UUID.class), any(BookRequest.class));
    }

    @Test
    void updateBook_ShouldReturnFailed() throws Exception{
        mockMvc.perform(put("/api/book/"+bookResponse.id().toString())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "LIBRARIAN"})
    void deleteBook_ShouldReturnSuccess() throws Exception{
        when(bookService.deleteBook(any(UUID.class))).thenReturn(bookResponse.id().toString()+ " Deleted Successfully");

        mockMvc.perform(delete("/api/book/"+bookResponse.id().toString())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookService).deleteBook(any(UUID.class));
    }

    @Test
    void deleteBook_ShouldReturnFailed() throws Exception{
        mockMvc.perform(delete("/api/book/"+bookResponse.id().toString())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
