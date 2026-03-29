package com.test.library_loan.member.model.entity;

import com.test.library_loan.member.model.enums.BorrowStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "MEMBER", indexes = {
        @Index(name = "idx_member", columnList = "MEMBER_NO, NAME, EMAIL")
})
public class Member {

    @Id
    @UuidGenerator
    @Column(name = "ID")
    private UUID id;

    @Column(name = "MEMBER_NO", nullable = false)
    private String memberNo;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "EMAIL", nullable = false)
    private String email;

    @Column(name = "REGISTER", nullable = false)
    private LocalDateTime register;

    @Column(name = "ACTIVE", nullable = false)
    private Boolean active;

    @Enumerated(EnumType.STRING)
    @Column(name = "BORROW_STATUS", nullable = false)
    private BorrowStatus borrowStatus;

    @Column(name = "DELETED", nullable = false)
    private Boolean deleted = Boolean.FALSE;
}
