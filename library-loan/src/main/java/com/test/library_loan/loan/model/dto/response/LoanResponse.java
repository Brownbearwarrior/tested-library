package com.test.library_loan.loan.model.dto.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.test.library_loan.book.model.dto.BookDTO;
import com.test.library_loan.member.model.dto.MemberDTO;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record LoanResponse(UUID id,
                           BookDTO book,
                           MemberDTO member,
                           LocalDateTime borrowedAt,
                           LocalDateTime dueDate,
                           LocalDateTime returnedAt) {
}