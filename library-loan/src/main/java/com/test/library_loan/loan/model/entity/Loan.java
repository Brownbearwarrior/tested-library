package com.test.library_loan.loan.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.test.library_loan.book.model.entity.Book;
import com.test.library_loan.member.model.entity.Member;
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
@Table(name = "LOAN", indexes = {
        @Index(name = "idx_loan", columnList = "BOOK_ID, MEMBER_ID")
})
public class Loan {

    @Id
    @UuidGenerator
    @Column(name = "ID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "BOOK_ID", nullable = false)
    @JsonIgnore
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MEMBER_ID", nullable = false)
    @JsonIgnore
    private Member member;

    @Column(name = "BORROWED_AT", nullable = false)
    private LocalDateTime borrowedAt;

    @Column(name = "DUE_DATE", nullable = false)
    private LocalDateTime dueDate;

    @Column(name = "RETURNED_AT")
    private LocalDateTime returnedAt;
}
