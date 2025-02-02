package com.karacam.bookie.core.models;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@Builder
public class PatternModel {
    private String pattern;
    private String localizationKey;
    private String errorCode;
}
