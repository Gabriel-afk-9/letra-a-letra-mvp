package com.letraaletra.api.presentation.dto.response.websocket;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.presentation.dto.response.game.GameStateDTO;

@JsonTypeName("PARTICIPANT_RECONNECTED")
public record ReconnectParticipantResponseDTO(
        GameStateDTO data
) implements WsResponseDTO {
}
