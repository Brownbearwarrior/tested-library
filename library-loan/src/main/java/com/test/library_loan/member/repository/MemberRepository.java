package com.test.library_loan.member.repository;

import com.test.library_loan.member.model.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {
    Member findByEmailAndDeleted(String email, Boolean deleted);
    Member findByIdAndDeleted(UUID id, Boolean deleted);
    List<Member> findByActiveAndDeleted(Boolean active, Boolean deleted);
    List<Member> findByDeleted(Boolean deleted);
}
