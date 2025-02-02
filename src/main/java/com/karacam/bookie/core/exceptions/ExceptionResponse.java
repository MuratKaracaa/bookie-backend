package com.karacam.bookie.core.exceptions;

import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionResponse<T> {
    @Setter(AccessLevel.NONE)
    private Instant timestamp = Instant.now();
    private String path;
    private T error;
    private List<T> errors;

    @Override
    public String toString() {
        return this.getTimestamp() + " " + this.getErrors() + " " + this.getError();
    }
}
