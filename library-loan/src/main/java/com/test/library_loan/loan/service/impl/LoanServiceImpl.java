package com.test.library_loan.loan.service.impl;

import com.test.library_loan.book.model.entity.Book;
import com.test.library_loan.book.service.BookDelegateService;
import com.test.library_loan.common.exception.ResourceNotFoundException;
import com.test.library_loan.common.properties.LoanProperties;
import com.test.library_loan.common.exception.BusinessException;
import com.test.library_loan.loan.model.dto.request.LoanRequest;
import com.test.library_loan.loan.model.dto.response.LoanResponse;
import com.test.library_loan.loan.model.entity.Loan;
import com.test.library_loan.loan.service.LoanDelegateService;
import com.test.library_loan.loan.service.LoanService;
import com.test.library_loan.loan.utils.LoanUtils;
import com.test.library_loan.member.model.entity.Member;
import com.test.library_loan.member.model.enums.BorrowStatus;
import com.test.library_loan.member.service.MemberDelegateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final LoanDelegateService loanDelegateService;
    private final BookDelegateService bookDelegateService;
    private final MemberDelegateService memberDelegateService;
    private final LoanProperties loanProperties;

    @Override
    @Transactional
    public LoanResponse borrowBook(LoanRequest loanRequest) {
        LocalDateTime now = LocalDateTime.now();

        var book = this.fetchValidBook(loanRequest.book().id());

        var member = this.fetchValidMember(loanRequest.member().id(), now);

        var loan = LoanUtils.convertLoan(book, member, now, loanProperties.dueDays());

        var saved = this.saveLoan(loan);

        this.updateAvailableCopies(book, "BORROW");

        this.updateStatusBorrow(member, BorrowStatus.ACTIVE);

        return LoanUtils.convertLoanResponse(saved);
    }

    @Override
    @Transactional
    public LoanResponse returnBook(UUID id) {
        LocalDateTime now = LocalDateTime.now();

        var exist = this.fetchDetail(id);

        exist.setReturnedAt(now);

        var saved = this.saveLoan(exist);

        this.updateAvailableCopies(exist.getBook(), "RETURN");

        this.validateUpdateStatusMember(exist.getMember());

        return LoanUtils.convertLoanResponse(saved);
    }

    @Override
    public List<LoanResponse> fetchAllLoan(UUID memberId, String status) {
        var loans = this.fetchLoans(memberId, status);

        return loans.stream().map(LoanUtils::convertLoanResponse).toList();
    }

    private Book fetchValidBook(UUID id){
        var book = this.fetchBook(id);

        this.validateBook(book);

        return book;
    }

    private Member fetchValidMember(UUID id, LocalDateTime now){
        var member = this.fetchMember(id);

        this.validateLoan(member, now);

        return member;
    }

    private void validateBook(Book book){
        if (Objects.isNull(book)){
            throw new ResourceNotFoundException("Book is not exist");
        }

        if (book.getAvailableCopies() <= 0){
            throw new BusinessException("No available copies");
        }
    }

    private void validateLoan(Member member, LocalDateTime now){
        var loans = fetchLoans(member.getId(), "ACTIVE");

        if (loans.isEmpty()){
            return;
        }

        var overdueLoans = loans.stream()
                .filter(loan -> loan.getDueDate().isBefore(now))
                .toList();

        if (loans.size() >= loanProperties.loans()){
            throw new BusinessException("Member already reach maximum active loans");
        }

        if (!overdueLoans.isEmpty()){
            throw new BusinessException("Member has overdue loans");
        }
    }

    private void validateUpdateStatusMember(Member member){
        var loans = this.fetchLoans(member.getId(), "ACTIVE");

        if (loans.isEmpty()){
            this.updateStatusBorrow(member, BorrowStatus.IN_ACTIVE);
        }
    }

    private void updateAvailableCopies(Book book, String type){
        if (type.equalsIgnoreCase("BORROW")){
            book.setAvailableCopies(book.getAvailableCopies() - 1);
        } else {
            book.setAvailableCopies(book.getAvailableCopies() + 1);
        }

        this.saveBook(book);
    }

    private void updateStatusBorrow(Member member, BorrowStatus borrowStatus){
        member.setBorrowStatus(borrowStatus);

        this.saveMember(member);
    }


    private Loan saveLoan(Loan loan){
        return loanDelegateService.createLoan(loan);
    }

    private Loan fetchDetail(UUID id){
        return loanDelegateService.fetchDetail(id);
    }

    private List<Loan> fetchLoans(UUID memberId, String status){
        return loanDelegateService.fetchLoans(null, memberId, status);
    }

    private Book fetchBook(UUID id){
        return bookDelegateService.fetchDetail(id, null);
    }

    private void saveBook(Book book){
        bookDelegateService.createBook(book);
    }

    private Member fetchMember(UUID id){
        return memberDelegateService.fetchDetail(id, null);
    }

    private void saveMember(Member member){
        memberDelegateService.createMember(member);
    }
}
