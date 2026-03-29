package com.test.library_loan.loan.service.impl;

import com.test.library_loan.book.model.dto.BookDTO;
import com.test.library_loan.book.model.entity.Book;
import com.test.library_loan.book.service.BookDelegateService;
import com.test.library_loan.common.exception.BusinessException;
import com.test.library_loan.common.exception.ResourceNotFoundException;
import com.test.library_loan.common.properties.LoanProperties;
import com.test.library_loan.loan.model.dto.request.LoanRequest;
import com.test.library_loan.loan.model.dto.response.LoanResponse;
import com.test.library_loan.loan.model.entity.Loan;
import com.test.library_loan.loan.service.LoanDelegateService;
import com.test.library_loan.member.model.dto.MemberDTO;
import com.test.library_loan.member.model.entity.Member;
import com.test.library_loan.member.model.enums.BorrowStatus;
import com.test.library_loan.member.service.MemberDelegateService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class LoanServiceImplTest {

    @Mock
    private BookDelegateService bookDelegateService;

    @Mock
    private MemberDelegateService memberDelegateService;

    @Mock
    private LoanDelegateService loanDelegateService;

    @Mock
    private LoanProperties loanProperties;

    @InjectMocks
    private LoanServiceImpl loanService;

    private Book book;
    private Member member;
    private Loan loan;
    private LoanRequest loanRequest;

    @BeforeEach
    void setUp(){
        LocalDateTime now = LocalDateTime.now();

        book = Book.builder()
                .id(UUID.randomUUID())
                .title("BOOK TITLE")
                .author("AUTHOR NAME")
                .isbn("1234-ASDF-QW45")
                .totalCopies(10)
                .availableCopies(10)
                .deleted(Boolean.FALSE)
                .build();

        member = Member.builder()
                .id(UUID.randomUUID())
                .name("NAME")
                .email("name@mail.com")
                .active(Boolean.TRUE)
                .memberNo("260329133128")
                .register(now)
                .borrowStatus(BorrowStatus.IN_ACTIVE)
                .deleted(Boolean.FALSE)
                .build();

        loan = Loan.builder()
                .id(UUID.randomUUID())
                .book(book)
                .member(member)
                .borrowedAt(now)
                .dueDate(now.plusDays(14))
                .build();

        BookDTO bookDTO = BookDTO.builder()
                .id(book.getId())
                .title("BOOK TITLE")
                .author("AUTHOR NAME")
                .isbn("1234-ASDF-QW45")
                .availableCopies(10)
                .build();

        MemberDTO memberDTO = MemberDTO.builder()
                .id(member.getId())
                .name("NAME")
                .email("name@mail.com")
                .memberNo("260329133128")
                .build();

        loanRequest = LoanRequest.builder()
                .book(bookDTO)
                .member(memberDTO)
                .build();
    }

    @Test
    void borrowBook_SuccessResponse(){
        when(bookDelegateService.fetchDetail(book.getId(), null)).thenReturn(book);
        when(memberDelegateService.fetchDetail(member.getId(), null)).thenReturn(member);
        when(loanDelegateService.fetchLoans(null, member.getId(), "ACTIVE")).thenReturn(new ArrayList<>());
        when(loanProperties.dueDays()).thenReturn(14);
        when(loanDelegateService.createLoan(any(Loan.class))).thenReturn(loan);
        when(bookDelegateService.createBook(any(Book.class))).thenReturn(book);
        when(memberDelegateService.createMember(any(Member.class))).thenReturn(member);

        LoanResponse result = loanService.borrowBook(loanRequest);

        assertNotNull(result);
        assertEquals(book.getId(), result.book().id());
        assertEquals(member.getId(), result.member().id());

        verify(bookDelegateService, times(1)).fetchDetail(book.getId(), null);
        verify(memberDelegateService, times(1)).fetchDetail(member.getId(), null);
        verify(loanDelegateService, times(1)).fetchLoans(null, member.getId(), "ACTIVE");
        verify(loanDelegateService, times(1)).createLoan(any(Loan.class));
        verify(bookDelegateService, times(1)).createBook(any(Book.class));
        verify(memberDelegateService, times(1)).createMember(any(Member.class));
    }

    @Test
    void borrowBook_InvalidBookNotFound(){
        when(bookDelegateService.fetchDetail(book.getId(), null)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> loanService.borrowBook(loanRequest));

        verify(bookDelegateService, times(1)).fetchDetail(book.getId(), null);
    }

    @Test
    void borrowBook_InvalidBookNotAvailable(){
        Book invalidBook = Book.builder()
                .id(loanRequest.book().id())
                .title("BOOK TITLE")
                .author("AUTHOR NAME")
                .isbn("1234-ASDF-QW45")
                .totalCopies(10)
                .availableCopies(0)
                .deleted(Boolean.FALSE)
                .build();

        when(bookDelegateService.fetchDetail(loanRequest.book().id(), null)).thenReturn(invalidBook);

        assertThrows(BusinessException.class, () -> loanService.borrowBook(loanRequest));

        verify(bookDelegateService, times(1)).fetchDetail(loanRequest.book().id(), null);
    }

    @Test
    void borrowBook_InvalidLimitActiveLoan(){
        List<Loan> loans = new ArrayList<>();
        loans.add(loan);
        loans.add(loan);
        loans.add(loan);

        when(bookDelegateService.fetchDetail(book.getId(), null)).thenReturn(book);
        when(memberDelegateService.fetchDetail(member.getId(), null)).thenReturn(member);
        when(loanDelegateService.fetchLoans(null, member.getId(), "ACTIVE")).thenReturn(loans);
        when(loanProperties.loans()).thenReturn(3);

        assertThrows(BusinessException.class, () -> loanService.borrowBook(loanRequest));

        verify(bookDelegateService, times(1)).fetchDetail(book.getId(), null);
        verify(memberDelegateService, times(1)).fetchDetail(member.getId(), null);
        verify(loanDelegateService, times(1)).fetchLoans(null, member.getId(), "ACTIVE");
    }

    @Test
    void borrowBook_InvalidOverdueLoan(){
        Loan overdueLoan = Loan.builder()
                .id(UUID.randomUUID())
                .book(book)
                .member(member)
                .borrowedAt(LocalDateTime.now().minusDays(16))
                .dueDate(LocalDateTime.now().minusDays(2))
                .build();

        List<Loan> loans = new ArrayList<>();
        loans.add(overdueLoan);

        when(bookDelegateService.fetchDetail(book.getId(), null)).thenReturn(book);
        when(memberDelegateService.fetchDetail(member.getId(), null)).thenReturn(member);
        when(loanDelegateService.fetchLoans(null, member.getId(), "ACTIVE")).thenReturn(loans);
        when(loanProperties.loans()).thenReturn(3);

        assertThrows(BusinessException.class, () -> loanService.borrowBook(loanRequest));

        verify(bookDelegateService, times(1)).fetchDetail(book.getId(), null);
        verify(memberDelegateService, times(1)).fetchDetail(member.getId(), null);
        verify(loanDelegateService, times(1)).fetchLoans(null, member.getId(), "ACTIVE");
    }


    /*
    LoanResponse returnBook(UUID id);
    List<LoanResponse> fetchAllLoan(UUID memberId, String status);
     */

}
