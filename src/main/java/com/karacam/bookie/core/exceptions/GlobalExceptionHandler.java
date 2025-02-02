package com.karacam.bookie.core.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse<NonValidFieldError>> handleValidationErrors(MethodArgumentNotValidException exception, ServletWebRequest request) {
        List<NonValidFieldError> errors = exception.getBindingResult().getAllErrors().stream()
                .map(objectError -> {
                    FieldError fieldError = (FieldError) objectError;
                    String[] split = fieldError.getField().split("\\.");
                    String field = split[0];
                    String errorCode = split[1];
                    return new NonValidFieldError(
                            field,
                            errorCode,
                            fieldError.getDefaultMessage());
                })
                .collect(Collectors.toList());

        ExceptionResponse<NonValidFieldError> exceptionResponse = new ExceptionResponse<>();
        exceptionResponse.setErrors(errors);
        exceptionResponse.setPath(getRequestPath(request));

        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ExceptionResponse<String>> handleUsernameNotFoundException(ResponseStatusException exception, ServletWebRequest request) {
        ExceptionResponse<String> exceptionResponse = new ExceptionResponse<String>();
        exceptionResponse.setPath(getRequestPath(request));
        exceptionResponse.setError(exception.getMessage());

        return new ResponseEntity<>(exceptionResponse, exception.getStatusCode());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ExceptionResponse<String>> handleRuntimeException(RuntimeException exception, ServletWebRequest request) {
        ExceptionResponse<String> exceptionResponse = new ExceptionResponse<String>();
        exceptionResponse.setPath(getRequestPath(request));
        exceptionResponse.setError(exception.getMessage());

        return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ExceptionResponse<String>> handleAuthenticationException(AuthenticationException exception, ServletWebRequest request) {
        ExceptionResponse<String> exceptionResponse = new ExceptionResponse<String>();
        exceptionResponse.setPath(getRequestPath(request));
        exceptionResponse.setError(exception.getMessage());

        return new ResponseEntity<>(exceptionResponse, HttpStatus.UNAUTHORIZED);
    }

    private String getRequestPath(ServletWebRequest request) {
        return request.getRequest().getRequestURI();
    }
}
