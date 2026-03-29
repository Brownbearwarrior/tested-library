package com.test.library_loan.member.controller;

import com.test.library_loan.common.model.Response;
import com.test.library_loan.common.utils.ResponseUtils;
import com.test.library_loan.loan.model.dto.response.LoanResponse;
import com.test.library_loan.loan.service.LoanService;
import com.test.library_loan.member.model.dto.request.MemberRequest;
import com.test.library_loan.member.model.dto.response.MemberResponse;
import com.test.library_loan.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final LoanService loanService;

    @PostMapping
    ResponseEntity<Response<MemberResponse>> createMember(@Valid @RequestBody MemberRequest memberRequest){
        return ResponseEntity.ok(
                ResponseUtils.toResponse("Success",
                        memberService.createMember(memberRequest)));
    }

    @GetMapping
    ResponseEntity<Response<List<MemberResponse>>> fetchAllMember(@RequestParam(required = false) String status){
        return ResponseEntity.ok(
                ResponseUtils.toResponse("Success",
                        memberService.fetchAllMember(status)));
    }

    @GetMapping("/{id}")
    ResponseEntity<Response<MemberResponse>> fetchDetailMember(@PathVariable UUID id){
        return ResponseEntity.ok(
                ResponseUtils.toResponse("Success",
                        memberService.fetchDetailMember(id)));
    }

    @GetMapping("/{id}/loans")
    ResponseEntity<Response<List<LoanResponse>>> fetchAllLoans(@PathVariable UUID id){
        return ResponseEntity.ok(
                ResponseUtils.toResponse("Success",
                        loanService.fetchAllLoan(id, null)));
    }

    @PutMapping("/{id}")
    ResponseEntity<Response<MemberResponse>> updateMember(@PathVariable UUID id,
                                                      @Valid @RequestBody MemberRequest memberRequest){
        return ResponseEntity.ok(
                ResponseUtils.toResponse("Success",
                        memberService.updateMember(id, memberRequest)));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Response<String>> deleteMember(@PathVariable UUID id){
        return ResponseEntity.ok(
                ResponseUtils.toResponse("Success",
                        memberService.deleteMember(id)));
    }
}
