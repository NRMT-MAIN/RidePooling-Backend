package com.nirmit.ride_pooling.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ApiErrorResponseDTO {
    private int status;
    private String errorCode;
    private String message;
    private LocalDateTime timestamp;
}

