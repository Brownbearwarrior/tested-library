package com.test.library_loan.member.service.impl;

import com.test.library_loan.member.model.entity.Member;
import com.test.library_loan.member.repository.MemberRepository;
import com.test.library_loan.member.service.MemberDelegateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberDelegateServiceImpl implements MemberDelegateService {

    private final MemberRepository memberRepository;

    @Override
    public Member createMember(Member member) {
        return memberRepository.save(member);
    }

    @Override
    public List<Member> fetchAllMember(Boolean status) {
        if (Objects.isNull(status)){
            return memberRepository.findByDeleted(Boolean.FALSE);
        }

        return memberRepository.findByActiveAndDeleted(status, Boolean.FALSE);
    }

    @Override
    public Member fetchDetail(UUID id, String email) {
        if (Objects.nonNull(id)){
            return memberRepository.findByIdAndDeleted(id, Boolean.FALSE);
        }

        return memberRepository.findByEmailAndDeleted(email, Boolean.FALSE);
    }
}
