package com.example.aml.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class BookException extends RuntimeException {

    private final List<ErrorModel> errorList;
    private final HttpStatus httpStatusCode;

    public BookException(List<ErrorModel> errorList, HttpStatus httpStatusCode) {
        if (Objects.isNull(errorList) || errorList.isEmpty()) {
            throw new IllegalArgumentException("Error list cannot be null or empty");
        }
        this.errorList = errorList;
        this.httpStatusCode = httpStatusCode;
    }

}
