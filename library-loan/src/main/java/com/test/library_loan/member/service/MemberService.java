package com.test.library_loan.member.service;

import com.test.library_loan.member.model.dto.request.MemberRequest;
import com.test.library_loan.member.model.dto.response.MemberResponse;

import java.util.List;
import java.util.UUID;

public interface MemberService {
    MemberResponse createMember(MemberRequest memberRequest);
    List<MemberResponse> fetchAllMember(String status);
    MemberResponse fetchDetailMember(UUID id);
    MemberResponse updateMember(UUID id, MemberRequest memberRequest);
    String deleteMember(UUID id);
}
