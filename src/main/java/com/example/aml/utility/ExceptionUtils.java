package com.example.aml.utility;

import com.example.aml.exception.BookException;
import com.example.aml.exception.ErrorCode;
import com.example.aml.exception.ErrorModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ExceptionUtils {
    private ExceptionUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static void throwBookException(HttpStatus httpStatusCode, ErrorCode errorCode, String message) {
        if (Objects.isNull(errorCode) || StringUtils.isBlank(message)) {
            throw new IllegalArgumentException("Code and message cannot be empty");
        }
        ArrayList<ErrorModel> errorList = new ArrayList<>();
        errorList.add(new ErrorModel(errorCode, message));
        throw new BookException(errorList, httpStatusCode);
    }

    public static void throwBookException(HttpStatus httpStatusCode, List<ErrorModel> errorList) {
        if (Objects.isNull(errorList) || errorList.isEmpty()) {
            throw new IllegalArgumentException("Error list cannot be null or empty");
        }
        throw new BookException(errorList, httpStatusCode);
    }

}
