package com.karacam.bookie.dtos.request;

import com.karacam.bookie.core.constraints.annotations.ValidField;
import com.karacam.bookie.core.enums.PatternCategory;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class OTPRequest {
    @ValidField(category = PatternCategory.DTO, patternKey = "otp_number")
    private String otpNumber;
}
