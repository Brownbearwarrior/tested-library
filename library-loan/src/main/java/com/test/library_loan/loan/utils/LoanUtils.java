package com.test.library_loan.loan.utils;

import com.test.library_loan.book.model.entity.Book;
import com.test.library_loan.book.utils.BookUtils;
import com.test.library_loan.loan.model.dto.response.LoanResponse;
import com.test.library_loan.loan.model.entity.Loan;
import com.test.library_loan.member.model.entity.Member;
import com.test.library_loan.member.utils.MemberUtils;

import java.time.LocalDateTime;

public class LoanUtils {
    private LoanUtils(){}

    public static Loan convertLoan(Book book, Member member, LocalDateTime now, int limitDueDate){
        return Loan.builder()
                .book(book)
                .member(member)
                .borrowedAt(now)
                .dueDate(now.plusDays(limitDueDate))
                .build();
    }

    public static LoanResponse convertLoanResponse(Loan loan){
        return LoanResponse.builder()
                .id(loan.getId())
                .book(BookUtils.convertBookDTO(loan.getBook()))
                .member(MemberUtils.convertMemberDTO(loan.getMember()))
                .borrowedAt(loan.getBorrowedAt())
                .dueDate(loan.getDueDate())
                .returnedAt(loan.getReturnedAt())
                .build();
    }
}
