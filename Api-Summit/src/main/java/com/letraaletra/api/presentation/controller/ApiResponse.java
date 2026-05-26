package com.letraaletra.api.presentation.controller;

import com.letraaletra.api.presentation.dto.response.http.SuccessResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiResponse {
    public static <T> ResponseEntity<SuccessResponseDTO<T>> success(
            T data,
            String message,
            HttpStatus status
    ) {
        return ResponseEntity
                .status(status)
                .body(new SuccessResponseDTO<>(
                        true,
                        message,
                        data
                ));
    }

    public static <T> ResponseEntity<SuccessResponseDTO<T>> success(T data, String message) {
        return success(data, message, HttpStatus.OK);
    }

    public static <T> ResponseEntity<SuccessResponseDTO<T>> success(T data) {
        return success(data, "ok", HttpStatus.OK);
    }
}
