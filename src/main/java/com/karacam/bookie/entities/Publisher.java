package com.karacam.bookie.entities;

import com.karacam.bookie.core.BaseEntity;
import com.karacam.bookie.core.Tables;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Entity(name = Tables.PUBLISHERS_TABLE)
public class Publisher extends BaseEntity {
    @Column(nullable = false, updatable = false)
    private String name;
}
