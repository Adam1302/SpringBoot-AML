package com.example.aml.exception;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ErrorModel {
    @NonNull
    private String code;

    @NonNull
    private String message;
}
