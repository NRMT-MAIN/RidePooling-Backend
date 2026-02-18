package com.nirmit.ride_pooling.utils.exceptions;

import org.springframework.http.HttpStatus;

public class ConflictException extends BaseExceptions {
    public ConflictException(String message) {
        super(message, HttpStatus.CONFLICT, "CONFLICT");
    }
}