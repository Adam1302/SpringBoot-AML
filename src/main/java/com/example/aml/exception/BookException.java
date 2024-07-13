package com.example.aml.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BookException extends RuntimeException {

    private List<ErrorModel> errorList;

    public BookException(List<ErrorModel> errorList) {
        this.errorList = errorList;
    }

}
