package com.karacam.bookie.dtos.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
public class RegisterUserResponse {
    private String verificationToken;
    private int tokenDuration;
}
