package com.example.aml.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BookException extends RuntimeException {

    private List<ErrorModel> errorList;
    private HttpStatus httpStatusCode;

    public BookException(List<ErrorModel> errorList, HttpStatus httpStatusCode) {
        this.errorList = errorList;
        this.httpStatusCode = httpStatusCode;
    }

}
