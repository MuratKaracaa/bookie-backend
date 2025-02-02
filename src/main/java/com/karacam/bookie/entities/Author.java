package com.karacam.bookie.entities;

import com.karacam.bookie.core.BaseEntity;
import com.karacam.bookie.core.Tables;
import com.karacam.bookie.core.constraints.annotations.ValidField;
import com.karacam.bookie.core.enums.PatternCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Entity(name = Tables.AUTHORS_TABLE)
public class Author extends BaseEntity {
    @Column(nullable = false, updatable = false)
    @ValidField(category = PatternCategory.ENTITY, patternKey = "alpha_numeric")
    private String name;
}
