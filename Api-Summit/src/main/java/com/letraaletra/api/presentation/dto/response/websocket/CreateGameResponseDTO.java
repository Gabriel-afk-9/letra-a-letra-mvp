package com.letraaletra.api.presentation.dto.response.websocket;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.presentation.dto.response.game.GameDTO;

@JsonTypeName("GAME_CREATED")
public record CreateGameResponseDTO(
        GameDTO data
) implements WsResponseDTO {
}
