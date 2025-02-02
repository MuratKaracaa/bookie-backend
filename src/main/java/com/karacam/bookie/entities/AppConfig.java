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
@Entity(name = Tables.CONFIGS_TABLE)
public class AppConfig {
    @Id
    private String key;
    @Column
    private String value;
}
