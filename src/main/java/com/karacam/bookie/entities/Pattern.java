package com.karacam.bookie.entities;

import com.karacam.bookie.core.Tables;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Entity(name = Tables.PATTERNS_TABLE)
public class Pattern {
    @Id
    private String key;
    @Column
    private String pattern;
    @Column(name = "loc_key")
    private String localizationKey;
    @Column(name = "error_code")
    private String errorCode;

}
