package com.nirmit.ride_pooling.utils.exceptions;

import com.nirmit.ride_pooling.dto.ApiErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseExceptions.class)
    public ResponseEntity<ApiErrorResponseDTO> handleBaseException(BaseExceptions ex) {

        ApiErrorResponseDTO response = ApiErrorResponseDTO.builder()
                        .status(ex.getStatus().value())
                        .errorCode(ex.getErrorCode())
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build();

        return ResponseEntity
                .status(ex.getStatus())
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleValidationException(MethodArgumentNotValidException ex) {

        String errorMessage =
                ex.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(field ->
                                field.getField()
                                        + ": "
                                        + field.getDefaultMessage())
                        .collect(Collectors.joining(", "));

        ApiErrorResponseDTO response = ApiErrorResponseDTO.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .errorCode("VALIDATION_ERROR")
                        .message(errorMessage)
                        .timestamp(LocalDateTime.now())
                        .build();

        return ResponseEntity
                .badRequest()
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponseDTO> handleGenericException(Exception ex) {

        ApiErrorResponseDTO response = ApiErrorResponseDTO.builder()
                        .status(500)
                        .errorCode("INTERNAL_SERVER_ERROR")
                        .message("Something went wrong")
                        .timestamp(LocalDateTime.now())
                        .build();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}
