package com.letraaletra.api.presentation.dto.response.websocket;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("ERROR")
public record ErrorResponseDTO(
        String message
) implements WsResponseDTO {}
