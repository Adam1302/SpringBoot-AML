package com.example.aml.utility;

import com.example.aml.exception.BookException;
import com.example.aml.exception.ErrorModel;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

public final class ExceptionUtils {
    private ExceptionUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static void throwBookException(HttpStatus httpStatusCode, String code, String message) {
        ArrayList<ErrorModel> errorList = new ArrayList<>();
        errorList.add(new ErrorModel(code, message));
        throw new BookException(errorList, httpStatusCode);
    }

    public static void throwBookException(HttpStatus httpStatusCode, List<ErrorModel> errorList) {
        throw new BookException(errorList, httpStatusCode);
    }

}
