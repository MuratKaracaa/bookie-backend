package com.karacam.bookie.dtos.request;

import com.karacam.bookie.core.constraints.annotations.ValidField;
import com.karacam.bookie.core.enums.PatternCategory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetOnePatternRequest {
    @ValidField(category = PatternCategory.DTO, patternKey = "alpha_numeric")
    private String key;
}
