package com.letraaletra.api.presentation.dto.response.websocket;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.presentation.dto.response.game.GameDTO;

@JsonTypeName("PARTICIPANT_LEAVE")
public record LeftGameResponseDTO(
        GameDTO data
) implements WsResponseDTO {
}
