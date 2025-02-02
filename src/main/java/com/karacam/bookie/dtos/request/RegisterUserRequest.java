package com.karacam.bookie.dtos.request;

import com.karacam.bookie.core.constraints.annotations.ValidField;
import com.karacam.bookie.core.enums.PatternCategory;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Data
public class RegisterUserRequest {
    @ValidField(category = PatternCategory.DTO, patternKey = "bookie_email")
    private String email;
    @ValidField(category = PatternCategory.DTO, patternKey = "dto_password")
    private String password;
    @ValidField(category = PatternCategory.DTO, patternKey = "alpha_numeric")
    private String firstName;
    @ValidField(category = PatternCategory.DTO, patternKey = "alpha_numeric")
    private String lastName;
}
