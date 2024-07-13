package com.example.aml.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(BookException.class)
    public ResponseEntity<List<ErrorModel>> handleBookException(BookException bookException) {
        return new ResponseEntity<List<ErrorModel>>
                (bookException.getErrorList(), bookException.getHttpStatusCode());
    }
}
