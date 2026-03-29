package com.test.library_loan.member.model.dto.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.test.library_loan.member.model.enums.BorrowStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record MemberResponse(UUID id,
                             String memberNo,
                             String name,
                             String email,
                             LocalDateTime register,
                             Boolean active,
                             BorrowStatus borrowStatus) {
}
