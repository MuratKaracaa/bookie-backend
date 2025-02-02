package com.karacam.bookie.core.exceptions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NonValidFieldError extends Error {
    private String field;

    public NonValidFieldError(String field, String errorCode, String message) {
        super(errorCode, message);
        this.field = field;
    }
}
