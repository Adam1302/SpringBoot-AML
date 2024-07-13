package com.example.aml.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@ControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(BookException.class)
    public ResponseEntity<List<ErrorModel>> handleBookException(BookException bookException) {
        Logger.getAnonymousLogger().log(
                Level.SEVERE,
                "BookException occurred: {}",
                bookException.getErrorList());
        return new ResponseEntity<>
                (bookException.getErrorList(), bookException.getHttpStatusCode());
    }
}
