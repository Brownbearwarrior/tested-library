package com.test.library_loan.loan.service;

import com.test.library_loan.loan.model.entity.Loan;

import java.util.List;
import java.util.UUID;

public interface LoanDelegateService {
    Loan createLoan(Loan loan);
    Loan fetchDetail(UUID id);
    List<Loan> fetchLoans(UUID bookId, UUID memberId, String status);
}
