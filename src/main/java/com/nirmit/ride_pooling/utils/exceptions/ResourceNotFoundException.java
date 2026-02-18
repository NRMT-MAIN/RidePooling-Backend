package com.nirmit.ride_pooling.utils.exceptions;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BaseExceptions{
    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND");
    }
}
