package com.letraaletra.api.presentation.dto.response.websocket;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("PARTICIPANT_DISCONNECTED")
public record DisconnectParticipantResponseDTO(
        String user
) implements WsResponseDTO {
}
