package com.test.library_loan.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.library_loan.book.model.dto.BookDTO;
import com.test.library_loan.common.config.SecurityConfig;
import com.test.library_loan.loan.model.dto.response.LoanResponse;
import com.test.library_loan.loan.service.LoanService;
import com.test.library_loan.member.model.dto.MemberDTO;
import com.test.library_loan.member.model.dto.request.MemberRequest;
import com.test.library_loan.member.model.dto.response.MemberResponse;
import com.test.library_loan.member.model.enums.BorrowStatus;
import com.test.library_loan.member.service.MemberService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
@Import({SecurityConfig.class})
class MemberControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    MemberService memberService;

    @MockitoBean
    LoanService loanService;

    private ObjectMapper objectMapper;
    private MemberRequest memberRequest;
    private MemberResponse memberResponse;
    private LoanResponse loanResponse;

    @BeforeEach
    void setUp(){
        objectMapper = new ObjectMapper();
        LocalDateTime now = LocalDateTime.now();

        memberRequest = MemberRequest.builder()
                .name("NAME")
                .email("name@mail.com")
                .active(Boolean.TRUE)
                .build();

        memberResponse = MemberResponse.builder()
                .id(UUID.randomUUID())
                .name("NAME")
                .email("name@mail.com")
                .active(Boolean.TRUE)
                .memberNo("260329133128")
                .register(now)
                .borrowStatus(BorrowStatus.IN_ACTIVE)
                .build();

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

        loanResponse = LoanResponse.builder()
                .id(UUID.randomUUID())
                .book(bookDTO)
                .member(memberDTO)
                .borrowedAt(now)
                .dueDate(now.plusDays(14))
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createMember_ShouldReturnSuccess() throws Exception{
        when(memberService.createMember(any(MemberRequest.class))).thenReturn(memberResponse);

        mockMvc.perform(post("/api/member")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("name@mail.com"));

        verify(memberService).createMember(any(MemberRequest.class));
    }

    @Test
    void createMember_ShouldReturnFailed() throws Exception{
        mockMvc.perform(post("/api/member")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "LIBRARIAN", "MEMBER"})
    void fetchMemberList_ShouldReturnSuccess() throws Exception{
        List<MemberResponse> memberResponses = new ArrayList<>();
        memberResponses.add(memberResponse);

        when(memberService.fetchAllMember(null)).thenReturn(memberResponses);

        mockMvc.perform(get("/api/member")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].email").value("name@mail.com"));

        verify(memberService).fetchAllMember(null);
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "LIBRARIAN", "MEMBER"})
    void fetchMemberListParamActive_ShouldReturnSuccess() throws Exception{
        List<MemberResponse> memberResponses = new ArrayList<>();
        memberResponses.add(memberResponse);

        when(memberService.fetchAllMember(anyString())).thenReturn(memberResponses);

        mockMvc.perform(get("/api/member")
                        .param("status", "ACTIVE")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].email").value("name@mail.com"));

        verify(memberService).fetchAllMember(anyString());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "LIBRARIAN", "MEMBER"})
    void fetchMemberListParamInActive_ShouldReturnSuccess() throws Exception{
        List<MemberResponse> memberResponses = new ArrayList<>();

        when(memberService.fetchAllMember(anyString())).thenReturn(memberResponses);

        mockMvc.perform(get("/api/member")
                        .param("status", "IN_ACTIVE")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(memberService).fetchAllMember(anyString());
    }

    @Test
    void fetchMemberList_ShouldReturnFailed() throws Exception{
        mockMvc.perform(get("/api/member")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "LIBRARIAN", "MEMBER"})
    void fetchMemberDetail_ShouldReturnSuccess() throws Exception{
        when(memberService.fetchDetailMember(any(UUID.class))).thenReturn(memberResponse);

        mockMvc.perform(get("/api/member/"+memberResponse.id().toString())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("name@mail.com"));

        verify(memberService).fetchDetailMember(any(UUID.class));
    }

    @Test
    void fetchMemberDetail_ShouldReturnFailed() throws Exception{
        mockMvc.perform(get("/api/member/"+memberResponse.id().toString())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "LIBRARIAN", "MEMBER"})
    void fetchMemberLoans_ShouldReturnSuccess() throws Exception{
        List<LoanResponse> loanResponses = new ArrayList<>();
        loanResponses.add(loanResponse);

        when(loanService.fetchAllLoan(memberResponse.id(), null)).thenReturn(loanResponses);

        mockMvc.perform(get("/api/member/"+memberResponse.id().toString()+"/loans")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].member.email").value("name@mail.com"));

        verify(loanService).fetchAllLoan(memberResponse.id(), null);
    }

    @Test
    void fetchMemberLoans_ShouldReturnFailed() throws Exception{
        mockMvc.perform(get("/api/member/"+memberResponse.id().toString()+"/loans")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateMember_ShouldReturnSuccess() throws Exception{
        when(memberService.updateMember(any(UUID.class), any(MemberRequest.class))).thenReturn(memberResponse);

        mockMvc.perform(put("/api/member/"+memberResponse.id().toString())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("name@mail.com"));

        verify(memberService).updateMember(any(UUID.class), any(MemberRequest.class));
    }

    @Test
    void updateMember_ShouldReturnFailed() throws Exception{
        mockMvc.perform(put("/api/member/"+memberResponse.id().toString())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteMember_ShouldReturnSuccess() throws Exception{
        when(memberService.deleteMember(any(UUID.class))).thenReturn(memberResponse.id().toString()+ " Deleted Successfully");

        mockMvc.perform(delete("/api/member/"+memberResponse.id().toString())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(memberService).deleteMember(any(UUID.class));
    }

    @Test
    void deleteMember_ShouldReturnFailed() throws Exception{
        mockMvc.perform(delete("/api/member/"+memberResponse.id().toString())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
