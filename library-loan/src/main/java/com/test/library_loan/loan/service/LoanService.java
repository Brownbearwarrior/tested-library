package com.test.library_loan.loan.service;

import com.test.library_loan.loan.model.dto.request.LoanRequest;
import com.test.library_loan.loan.model.dto.response.LoanResponse;

import java.util.List;
import java.util.UUID;

public interface LoanService {
    LoanResponse borrowBook(LoanRequest loanRequest);
    LoanResponse returnBook(UUID id);
    List<LoanResponse> fetchAllLoan(UUID memberId, String status);
}
