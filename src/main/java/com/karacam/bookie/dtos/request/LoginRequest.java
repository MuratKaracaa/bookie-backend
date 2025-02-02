package com.karacam.bookie.dtos.request;

import com.karacam.bookie.core.constraints.annotations.ValidField;
import com.karacam.bookie.core.enums.PatternCategory;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class LoginRequest {
    @ValidField(category = PatternCategory.DTO, patternKey = "bookie_email")
    private String email;
    @ValidField(category = PatternCategory.DTO, patternKey = "dto_password")
    private String password;
}
