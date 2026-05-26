package com.letraaletra.api.presentation.dto.response.http;

public record SuccessResponseDTO<T>(
        boolean success,
        String message,
        T data
) {
}
