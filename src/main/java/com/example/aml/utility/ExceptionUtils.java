package com.example.aml.utility;

import com.example.aml.exception.BookException;
import com.example.aml.exception.ErrorModel;

import java.util.ArrayList;
import java.util.List;

public final class ExceptionUtils {
    private ExceptionUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static void throwBookException(String code, String message) {
        ArrayList<ErrorModel> errorList = new ArrayList<>();
        errorList.add(new ErrorModel(code, message));
        throw new BookException(errorList);
    }

    public static void throwBookException(List<ErrorModel> errorList) {
        throw new BookException(errorList);
    }

}
