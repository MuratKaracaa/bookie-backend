package com.karacam.bookie.core;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.stereotype.Component;

import java.time.Instant;


@Component
public class TimeStampListener {
    @PrePersist
    public void setCreatedAt(BaseEntity entity) {
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());
    }

    @PreUpdate
    public void setUpdatedAt(BaseEntity entity) {
        entity.setUpdatedAt(Instant.now());
    }
}
