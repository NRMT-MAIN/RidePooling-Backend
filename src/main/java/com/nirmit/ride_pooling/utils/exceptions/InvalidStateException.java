package com.nirmit.ride_pooling.utils.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidStateException extends BaseExceptions {
    public InvalidStateException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "INVALID_STATE");
    }
}