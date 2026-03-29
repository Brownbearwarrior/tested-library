package com.test.library_loan.member.service.impl;

import com.test.library_loan.common.exception.BusinessException;
import com.test.library_loan.common.exception.ResourceNotFoundException;
import com.test.library_loan.member.model.dto.request.MemberRequest;
import com.test.library_loan.member.model.dto.response.MemberResponse;
import com.test.library_loan.member.model.entity.Member;
import com.test.library_loan.member.model.enums.BorrowStatus;
import com.test.library_loan.member.service.MemberDelegateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @Mock
    private MemberDelegateService memberDelegateService;

    @InjectMocks
    private MemberServiceImpl memberService;

    private Member member;
    private MemberRequest memberRequest;

    @BeforeEach
    void setUp(){
        LocalDateTime now = LocalDateTime.now();

        member = Member.builder()
                .id(UUID.randomUUID())
                .name("NAME")
                .email("name@mail.com")
                .active(Boolean.TRUE)
                .memberNo("260329133128")
                .register(now)
                .borrowStatus(BorrowStatus.IN_ACTIVE)
                .deleted(Boolean.FALSE)
                .build();

        memberRequest = MemberRequest.builder()
                .name("NAME")
                .email("name@mail.com")
                .active(Boolean.TRUE)
                .build();
    }

    @Test
    void createMember_SuccessResponse(){
        when(memberDelegateService.fetchDetail(null, memberRequest.email())).thenReturn(null);
        when(memberDelegateService.createMember(any(Member.class))).thenReturn(member);

        MemberResponse result =  memberService.createMember(memberRequest);

        assertNotNull(result);
        assertEquals(member.getEmail(), result.email());

        verify(memberDelegateService, times(1)).fetchDetail(null, memberRequest.email());
        verify(memberDelegateService, times(1)).createMember(any(Member.class));
    }

    @Test
    void createMember_InvalidExist(){
        when(memberDelegateService.fetchDetail(null, memberRequest.email())).thenReturn(member);

        assertThrows(BusinessException.class, () -> memberService.createMember(memberRequest));

        verify(memberDelegateService, times(1)).fetchDetail(null, memberRequest.email());
    }

    @Test
    void fetchAllMember_SuccessResponse(){
        List<Member> members = new ArrayList<>();
        members.add(member);

        when(memberDelegateService.fetchAllMember(null)).thenReturn(members);

        List<MemberResponse> result =  memberService.fetchAllMember(null);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(memberDelegateService, times(1)).fetchAllMember(null);
    }

    @Test
    void fetchAllMember_StatusActive(){
        List<Member> members = new ArrayList<>();
        members.add(member);

        when(memberDelegateService.fetchAllMember(Boolean.TRUE)).thenReturn(members);

        List<MemberResponse> result =  memberService.fetchAllMember("ACTIVE");

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(memberDelegateService, times(1)).fetchAllMember(Boolean.TRUE);
    }

    @Test
    void fetchAllMember_StatusInActive(){
        List<Member> members = new ArrayList<>();
        members.add(member);

        when(memberDelegateService.fetchAllMember(Boolean.FALSE)).thenReturn(members);

        List<MemberResponse> result =  memberService.fetchAllMember("IN_ACTIVE");

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(memberDelegateService, times(1)).fetchAllMember(Boolean.FALSE);
    }

    @Test
    void fetchDetailMember_SuccessResponse(){
        when(memberDelegateService.fetchDetail(member.getId(), null)).thenReturn(member);

        MemberResponse result = memberService.fetchDetailMember(member.getId());

        assertNotNull(result);
        assertEquals(member.getId(), result.id());

        verify(memberDelegateService, times(1)).fetchDetail(member.getId(), null);
    }

    @Test
    void fetchDetailMember_NotFound(){
        when(memberDelegateService.fetchDetail(member.getId(), null)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> memberService.fetchDetailMember(member.getId()));

        verify(memberDelegateService, times(1)).fetchDetail(member.getId(), null);
    }

    @Test
    void updateMember_SuccessResponse(){
        when(memberDelegateService.fetchDetail(null, memberRequest.email())).thenReturn(member);
        when(memberDelegateService.fetchDetail(member.getId(), null)).thenReturn(member);
        when(memberDelegateService.createMember(any(Member.class))).thenReturn(member);

        MemberResponse result = memberService.updateMember(member.getId(), memberRequest);

        assertNotNull(result);
        assertEquals(member.getEmail(), result.email());
        assertEquals(member.getId(), result.id());

        verify(memberDelegateService, times(1)).fetchDetail(null, memberRequest.email());
        verify(memberDelegateService, times(1)).fetchDetail(member.getId(), null);
        verify(memberDelegateService, times(1)).createMember(any(Member.class));
    }

    @Test
    void updateMember_InvalidAlreadyExist(){
        when(memberDelegateService.fetchDetail(null, memberRequest.email())).thenReturn(member);

        assertThrows(BusinessException.class, () -> memberService.updateMember(UUID.randomUUID(), memberRequest));

        verify(memberDelegateService, times(1)).fetchDetail(null, memberRequest.email());
    }

    @Test
    void updateMember_NotFound(){
        when(memberDelegateService.fetchDetail(null, memberRequest.email())).thenReturn(null);
        when(memberDelegateService.fetchDetail(member.getId(), null)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> memberService.updateMember(member.getId(), memberRequest));

        verify(memberDelegateService, times(1)).fetchDetail(null, memberRequest.email());
        verify(memberDelegateService, times(1)).fetchDetail(member.getId(), null);
    }

    @Test
    void deleteMember_SuccessResponse(){
        when(memberDelegateService.fetchDetail(member.getId(), null)).thenReturn(member);
        when(memberDelegateService.createMember(any(Member.class))).thenReturn(member);

        String result = memberService.deleteMember(member.getId());

        assertNotNull(result);

        verify(memberDelegateService, times(1)).fetchDetail(member.getId(), null);
        verify(memberDelegateService, times(1)).createMember(any(Member.class));
    }

    @Test
    void deleteMember_NotFound(){
        when(memberDelegateService.fetchDetail(member.getId(), null)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> memberService.deleteMember(member.getId()));

        verify(memberDelegateService, times(1)).fetchDetail(member.getId(), null);
    }

    @Test
    void deleteMember_InvalidBorrowed(){
        Member invalidMember = Member.builder()
                .id(UUID.randomUUID())
                .name("NAME")
                .email("name@mail.com")
                .active(Boolean.TRUE)
                .memberNo("260329133128")
                .register(LocalDateTime.now())
                .borrowStatus(BorrowStatus.ACTIVE)
                .deleted(Boolean.FALSE)
                .build();

        when(memberDelegateService.fetchDetail(invalidMember.getId(), null)).thenReturn(invalidMember);

        assertThrows(BusinessException.class, () -> memberService.deleteMember(invalidMember.getId()));

        verify(memberDelegateService, times(1)).fetchDetail(invalidMember.getId(), null);
    }
}
