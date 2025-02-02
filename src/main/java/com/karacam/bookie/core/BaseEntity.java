package com.karacam.bookie.core;


import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;


@Data
@MappedSuperclass
@EntityListeners(TimeStampListener.class)
public class BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Instant updatedAt;
    @Column(updatable = false, nullable = false)
    private Instant createdAt;
}
