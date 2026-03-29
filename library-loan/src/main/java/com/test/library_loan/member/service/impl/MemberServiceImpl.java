package com.test.library_loan.member.service.impl;

import com.test.library_loan.common.exception.BusinessException;
import com.test.library_loan.common.exception.ResourceNotFoundException;
import com.test.library_loan.member.model.dto.request.MemberRequest;
import com.test.library_loan.member.model.dto.response.MemberResponse;
import com.test.library_loan.member.model.entity.Member;
import com.test.library_loan.member.model.enums.BorrowStatus;
import com.test.library_loan.member.service.MemberDelegateService;
import com.test.library_loan.member.service.MemberService;
import com.test.library_loan.member.utils.MemberUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberDelegateService memberDelegateService;

    @Override
    @Transactional
    public MemberResponse createMember(MemberRequest memberRequest) {
        Member member = MemberUtils.convertMember(memberRequest);

        var exist = this.fetchMember(null, member.getEmail());

        if (Objects.nonNull(exist)){
            throw new BusinessException("Member already exist");
        }

        var saved = this.saveMember(member);

        return MemberUtils.convertMemberResponse(saved);
    }

    @Override
    public List<MemberResponse> fetchAllMember(String status) {
        var members = this.fetchMembers(status);

        return members.stream().map(MemberUtils::convertMemberResponse).toList();
    }

    @Override
    public MemberResponse fetchDetailMember(UUID id) {
        var exist = this.fetchExistMember(id);

        return MemberUtils.convertMemberResponse(exist);
    }

    @Override
    @Transactional
    public MemberResponse updateMember(UUID id, MemberRequest memberRequest) {
        Member request = MemberUtils.convertMember(memberRequest);

        var check = this.fetchMember(null, request.getEmail());

        if (Objects.nonNull(check) && check.getId() != id){
            throw new BusinessException("Member already exist");
        }

        var exist = this.fetchExistMember(id);

        exist.setName(request.getName());
        exist.setEmail(request.getEmail());
        exist.setActive(request.getActive());

        var updated = this.saveMember(exist);

        return MemberUtils.convertMemberResponse(updated);
    }

    @Override
    @Transactional
    public String deleteMember(UUID id) {
        var exist = this.fetchExistMember(id);

        this.validateDeleteMember(exist);

        exist.setDeleted(Boolean.TRUE);

        this.saveMember(exist);

        return id.toString() + " Deleted Successfully";
    }

    private Member fetchExistMember(UUID id){
        var member = this.fetchMember(id, null);

        if (Objects.isNull(member)){
            throw new ResourceNotFoundException("Member not found");
        }

        return member;
    }

    private void validateDeleteMember(Member member){
        if (member.getBorrowStatus() == BorrowStatus.ACTIVE){
            throw new BusinessException("Member has active loans");
        }
    }

    private Member saveMember(Member member){
        return memberDelegateService.createMember(member);
    }

    private List<Member> fetchMembers(String status){
        if (Objects.isNull(status)){
            return memberDelegateService.fetchAllMember(null);
        } else if (status.equalsIgnoreCase("ACTIVE")){
            return memberDelegateService.fetchAllMember(Boolean.TRUE);
        }
        return memberDelegateService.fetchAllMember(Boolean.FALSE);
    }

    private Member fetchMember(UUID id, String email){
        return memberDelegateService.fetchDetail(id, email);
    }
}
