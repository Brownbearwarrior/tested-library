package com.test.library_loan.loan.controller;

import com.test.library_loan.common.model.Response;
import com.test.library_loan.common.utils.ResponseUtils;
import com.test.library_loan.loan.model.dto.request.LoanRequest;
import com.test.library_loan.loan.model.dto.response.LoanResponse;
import com.test.library_loan.loan.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/loan")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PreAuthorize("hasRole('MEMBER')")
    @PostMapping("/borrow")
    ResponseEntity<Response<LoanResponse>> borrowBook(@Valid @RequestBody LoanRequest loanRequest){
        return ResponseEntity.ok(ResponseUtils.toResponse("Success",
                loanService.borrowBook(loanRequest)));
    }

    @PreAuthorize("hasRole('MEMBER')")
    @PutMapping("/return/{id}")
    ResponseEntity<Response<LoanResponse>> returnBook(@PathVariable UUID id){
        return ResponseEntity.ok(ResponseUtils.toResponse("Success",
                loanService.returnBook(id)));
    }

    @PreAuthorize("hasAnyRole('LIBRARIAN', 'MEMBER')")
    @GetMapping()
    ResponseEntity<Response<List<LoanResponse>>> fetchAllLoans(@RequestParam(required = false) String status){
        return ResponseEntity.ok(ResponseUtils.toResponse("Success",
                loanService.fetchAllLoan(null, status)));
    }
}
