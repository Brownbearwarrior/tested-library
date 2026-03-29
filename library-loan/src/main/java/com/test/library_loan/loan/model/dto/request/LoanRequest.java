package com.test.library_loan.loan.model.dto.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.test.library_loan.book.model.dto.BookDTO;
import com.test.library_loan.member.model.dto.MemberDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record LoanRequest(@NotNull BookDTO book,
                          @NotNull MemberDTO member) {
}
