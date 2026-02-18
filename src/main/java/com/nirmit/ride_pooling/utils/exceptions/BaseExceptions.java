package com.nirmit.ride_pooling.utils.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class BaseExceptions extends RuntimeException {
    private final HttpStatus status;
    private final String errorCode;

    protected BaseExceptions(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }
}
