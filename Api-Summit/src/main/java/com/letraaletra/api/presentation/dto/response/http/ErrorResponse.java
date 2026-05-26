package com.letraaletra.api.presentation.dto.response.http;

public record ErrorResponse(
        boolean success,
        String message
) {
}
