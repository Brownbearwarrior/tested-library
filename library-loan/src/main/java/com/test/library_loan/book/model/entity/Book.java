package com.test.library_loan.book.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "BOOK", indexes = {
        @Index(name = "idx_book", columnList = "TITLE, AUTHOR, ISBN")
})
public class Book {

    @Id
    @UuidGenerator
    @Column(name = "ID")
    private UUID id;

    @Column(name = "TITLE", nullable = false)
    private String title;

    @Column(name = "AUTHOR", nullable = false)
    private String author;

    @Column(name = "ISBN", nullable = false)
    private String isbn;

    @Column(name = "TOTAL_COPIES", nullable = false)
    private Integer totalCopies;

    @Column(name = "AVAILABLE_COPIES", nullable = false)
    private Integer availableCopies;

    @Column(name = "DELETED", nullable = false)
    private Boolean deleted = Boolean.FALSE;
}
