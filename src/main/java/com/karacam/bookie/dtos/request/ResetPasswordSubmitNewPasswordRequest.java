package com.karacam.bookie.dtos.request;

import com.karacam.bookie.core.constraints.annotations.ValidField;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ResetPasswordSubmitNewPasswordRequest {
    @ValidField(patternKey = "dto_password")
    private String oldPassword;
    @ValidField(patternKey = "dto_password")
    private String newPassword;
}
