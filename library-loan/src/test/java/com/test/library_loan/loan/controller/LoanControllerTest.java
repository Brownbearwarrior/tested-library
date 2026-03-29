package com.test.library_loan.loan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.library_loan.book.model.dto.BookDTO;
import com.test.library_loan.common.config.SecurityConfig;
import com.test.library_loan.loan.model.dto.request.LoanRequest;
import com.test.library_loan.loan.model.dto.response.LoanResponse;
import com.test.library_loan.loan.service.LoanService;
import com.test.library_loan.member.model.dto.MemberDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
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

@WebMvcTest(LoanController.class)
@Import({SecurityConfig.class})
class LoanControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    LoanService loanService;

    private ObjectMapper objectMapper;
    private LoanRequest loanRequest;
    private LoanResponse loanResponse;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        LocalDateTime now = LocalDateTime.now();

        BookDTO bookDTO = BookDTO.builder()
                .id(UUID.randomUUID())
                .title("BOOK TITLE")
                .author("AUTHOR NAME")
                .isbn("1234-ASDF-QW45")
                .availableCopies(10)
                .build();

        MemberDTO memberDTO = MemberDTO.builder()
                .id(UUID.randomUUID())
                .name("NAME")
                .email("name@mail.com")
                .memberNo("260329133128")
                .build();

        loanRequest = LoanRequest.builder()
                .book(bookDTO)
                .member(memberDTO)
                .build();

        loanResponse = LoanResponse.builder()
                .id(UUID.randomUUID())
                .book(bookDTO)
                .member(memberDTO)
                .borrowedAt(now)
                .dueDate(now.plusDays(14))
                .build();
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    void createBorrow_ShouldReturnSuccess() throws Exception{
        when(loanService.borrowBook(any(LoanRequest.class))).thenReturn(loanResponse);

        mockMvc.perform(post("/api/loan/borrow")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.member.email").value("name@mail.com"));

        verify(loanService).borrowBook(any(LoanRequest.class));
    }

    @Test
    void createBorrow_ShouldReturnFailed() throws Exception{
        mockMvc.perform(post("/api/loan/borrow")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    void updateReturn_ShouldReturnSuccess() throws Exception{
        when(loanService.returnBook(any(UUID.class))).thenReturn(loanResponse);

        mockMvc.perform(put("/api/loan/return/"+loanResponse.id().toString())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.member.email").value("name@mail.com"));

        verify(loanService).returnBook(any(UUID.class));
    }

    @Test
    void updateReturn_ShouldReturnFailed() throws Exception{
        mockMvc.perform(put("/api/loan/return/"+loanResponse.id().toString())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"LIBRARIAN", "MEMBER"})
    void fetchLoanList_ShouldReturnSuccess() throws Exception{
        List<LoanResponse> loanResponses = new ArrayList<>();
        loanResponses.add(loanResponse);

        when(loanService.fetchAllLoan(null, null)).thenReturn(loanResponses);

        mockMvc.perform(get("/api/loan")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].member.email").value("name@mail.com"));

        verify(loanService).fetchAllLoan(null, null);
    }

    @Test
    @WithMockUser(roles = {"LIBRARIAN", "MEMBER"})
    void fetchLoanListParamActive_ShouldReturnSuccess() throws Exception{
        List<LoanResponse> loanResponses = new ArrayList<>();
        loanResponses.add(loanResponse);

        when(loanService.fetchAllLoan(null, "ACTIVE")).thenReturn(loanResponses);

        mockMvc.perform(get("/api/loan")
                        .param("status", "ACTIVE")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].member.email").value("name@mail.com"));

        verify(loanService).fetchAllLoan(null, "ACTIVE");
    }

    @Test
    @WithMockUser(roles = {"LIBRARIAN", "MEMBER"})
    void fetchLoanListParamInActive_ShouldReturnSuccess() throws Exception{
        List<LoanResponse> loanResponses = new ArrayList<>();
        loanResponses.add(loanResponse);

        when(loanService.fetchAllLoan(null, "IN_ACTIVE")).thenReturn(loanResponses);

        mockMvc.perform(get("/api/loan")
                        .param("status", "IN_ACTIVE")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].member.email").value("name@mail.com"));

        verify(loanService).fetchAllLoan(null, "IN_ACTIVE");
    }

    @Test
    void fetchLoanList_ShouldReturnFailed() throws Exception{
        mockMvc.perform(get("/api/loan")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
