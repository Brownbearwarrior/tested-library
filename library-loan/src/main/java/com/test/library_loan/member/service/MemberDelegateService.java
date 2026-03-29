package com.test.library_loan.member.service;

import com.test.library_loan.member.model.entity.Member;

import java.util.List;
import java.util.UUID;

public interface MemberDelegateService {
    Member createMember(Member member);
    List<Member> fetchAllMember(Boolean status);
    Member fetchDetail(UUID id, String email);
}
