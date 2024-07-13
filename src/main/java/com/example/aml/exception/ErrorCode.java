package com.example.aml.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    ALREADY_EXISTS("ALREADY_EXISTS", "The entry you're trying to create already exists."),
    BACKEND_ERROR("BACKEND_ERROR", "An error occured in our service. We're working on getting it fixed."),
    EMPTY_VALUE_NOT_ALLOWED("EMPTY_VALUE_NOT_ALLOWED", "Empty values aren't allowed for this field."),
    ENTRY_WITH_ID_NOT_FOUND("ENTRY_WITH_ID_NOT_FOUND", "No entry with this ID was found."),
    INVALID_FIELD("INVALID_FIELD", "You entered an invalid field value"),
    NEGATIVE_VALUE_NOT_ALLOWED("NEGATIVE_VALUE_NOT_ALLOWED", "You cannot enter a negative value."),
    ;

    private final String key;
    private final String message;

    ErrorCode(String key, String message) {
        this.key = key;
        this.message = message;
    }
}
