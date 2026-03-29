package com.test.library_loan.loan.repository;

import com.test.library_loan.loan.model.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LoanRepository extends JpaRepository<Loan, UUID> {
    List<Loan> findByBook_Id(UUID bookId);
    List<Loan> findByMember_Id(UUID memberId);
    List<Loan> findByBook_IdAndReturnedAtIsNull(UUID bookId);
    List<Loan> findByMember_IdAndReturnedAtIsNull(UUID memberId);
    List<Loan> findByBook_IdAndReturnedAtIsNotNull(UUID bookId);
    List<Loan> findByMember_IdAndReturnedAtIsNotNull(UUID memberId);
}
