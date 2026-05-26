package com.letraaletra.api.presentation.dto.response.websocket;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.presentation.dto.response.game.GameStateDTO;

@JsonTypeName("GAME_STARTED")
public record StartGameResponseDTO(
        GameStateDTO data
) implements WsResponseDTO {
}
