package com.karacam.bookie.dtos.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
@Builder
public class AuthResponse {
    private String sessionId;
}
