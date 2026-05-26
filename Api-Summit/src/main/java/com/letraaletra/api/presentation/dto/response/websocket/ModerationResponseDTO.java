package com.letraaletra.api.presentation.dto.response.websocket;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("MODERATION_MESSAGE")
public record ModerationResponseDTO(
        String warning
) implements WsResponseDTO {
}
