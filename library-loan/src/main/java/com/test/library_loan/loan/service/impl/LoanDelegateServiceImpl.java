package com.test.library_loan.loan.service.impl;

import com.test.library_loan.loan.model.entity.Loan;
import com.test.library_loan.loan.repository.LoanRepository;
import com.test.library_loan.loan.service.LoanDelegateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoanDelegateServiceImpl implements LoanDelegateService {

    private final LoanRepository loanRepository;

    @Override
    public Loan createLoan(Loan loan) {
        return loanRepository.save(loan);
    }

    @Override
    public Loan fetchDetail(UUID id) {
        return loanRepository.findById(id).orElse(null);
    }

    @Override
    public List<Loan> fetchLoans(UUID bookId, UUID memberId, String status) {
        if (Objects.isNull(status)){
            if (Objects.nonNull(bookId)){
                return loanRepository.findByBook_Id(bookId);
            } else {
                return loanRepository.findByMember_Id(memberId);
            }
        } else if (status.equalsIgnoreCase("ACTIVE")){
            if (Objects.nonNull(bookId)){
                return loanRepository.findByBook_IdAndReturnedAtIsNull(bookId);
            } else {
                return loanRepository.findByMember_IdAndReturnedAtIsNull(memberId);
            }
        } else {
            if (Objects.nonNull(bookId)){
                return loanRepository.findByBook_IdAndReturnedAtIsNotNull(bookId);
            } else {
                return loanRepository.findByMember_IdAndReturnedAtIsNotNull(memberId);
            }
        }
    }
}
