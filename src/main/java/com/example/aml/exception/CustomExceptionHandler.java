package com.example.aml.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.example.aml.exception.ErrorCode.INVALID_FIELD;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ErrorModel>> handleFieldValidation(MethodArgumentNotValidException invalidArgException) {
        List<ErrorModel> errorList = new ArrayList<>();
        ErrorModel errorModel = null;
        List<FieldError> fieldErrorList = invalidArgException.getBindingResult().getFieldErrors();

        for (FieldError fe : fieldErrorList) {
            errorModel = new ErrorModel(INVALID_FIELD, Objects.requireNonNull(fe.getDefaultMessage()));
            errorList.add(errorModel);
        }
        return new ResponseEntity<>(errorList, HttpStatus.NOT_ACCEPTABLE);
}
}
