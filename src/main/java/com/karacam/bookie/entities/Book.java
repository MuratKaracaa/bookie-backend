package com.karacam.bookie.entities;

import com.karacam.bookie.core.BaseEntity;
import com.karacam.bookie.core.Tables;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Entity(name = Tables.BOOKS_TABLE)
public class Book extends BaseEntity {

    @Column(nullable = false, updatable = false)
    private String title;

    @Column(nullable = false, updatable = false)
    private String author;

    @Column(nullable = false, updatable = false)
    private String publisher;

    @Column(updatable = false, nullable = false)
    private String description;

    @Column(updatable = false)
    private String isbn;

    @Column(nullable = false)
    private int fileSize;

    @Column(nullable = false)
    private short pageCount;

    @Column(nullable = false)
    private Instant releaseDate;
}
