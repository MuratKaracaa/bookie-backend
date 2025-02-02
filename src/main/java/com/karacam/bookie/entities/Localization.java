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
@Entity(name = Tables.LOCALIZATIONS_TABLE)
public class Localization {
    @Id
    private String key;
    @Column(name = "tr_tr")
    private String trTr;
    @Column(name = "en_us")
    private String enUs;
}
