package com.letraaletra.api.presentation.dto.response.http;

import com.letraaletra.api.domain.MessageCode;

public enum ServerMessages implements MessageCode {
    INVALID_INPUT("invalid_input"),
    INTERNAL_ERROR("internal_error"),
    SERVICE_UNAVAILABLE("service_unavailable"),
    TIMEOUT("timeout"),
    UNAUTHORIZED("unauthorized"),
    FORBIDDEN("forbidden"),
    INVALID_ID("invalid_id");

    private final String message;

    ServerMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}