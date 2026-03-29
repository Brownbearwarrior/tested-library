package com.test.library_loan.member.utils;

import com.test.library_loan.member.model.dto.MemberDTO;
import com.test.library_loan.member.model.dto.request.MemberRequest;
import com.test.library_loan.member.model.dto.response.MemberResponse;
import com.test.library_loan.member.model.entity.Member;
import com.test.library_loan.member.model.enums.BorrowStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MemberUtils {
    private MemberUtils(){}

    public static Member convertMember(MemberRequest memberRequest){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmmss");

        return Member.builder()
                .name(memberRequest.name().toUpperCase())
                .email(memberRequest.email().toLowerCase())
                .active(memberRequest.active())
                .memberNo(now.format(formatter))
                .register(now)
                .borrowStatus(BorrowStatus.IN_ACTIVE)
                .deleted(Boolean.FALSE)
                .build();
    }

    public static MemberResponse convertMemberResponse(Member member){
        return MemberResponse.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .memberNo(member.getMemberNo())
                .register(member.getRegister())
                .active(member.getActive())
                .borrowStatus(member.getBorrowStatus())
                .build();
    }

    public static MemberDTO convertMemberDTO(Member member){
        return MemberDTO.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .memberNo(member.getMemberNo())
                .build();
    }
}
