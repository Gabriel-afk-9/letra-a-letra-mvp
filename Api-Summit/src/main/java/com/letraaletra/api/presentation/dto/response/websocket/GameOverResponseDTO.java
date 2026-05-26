package com.letraaletra.api.presentation.dto.response.websocket;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.presentation.dto.response.game.GameOverDTO;

@JsonTypeName("GAME_OVER")
public record GameOverResponseDTO(
        GameOverDTO data
) implements WsResponseDTO {
}
