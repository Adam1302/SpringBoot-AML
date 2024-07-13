package com.example.aml.exception;

import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

@Getter
public class ErrorModel {
    @NonNull
    private ErrorCode errorCode;

    @NonNull
    private String extraInfo;

    public ErrorModel(@NonNull ErrorCode errorCode, @NonNull String extraInfo) {
        if (StringUtils.isBlank(extraInfo)) {
            throw new IllegalArgumentException("Error code shouldn't be null");
        }
        this.errorCode = errorCode;
        this.extraInfo = extraInfo;
    }
}
