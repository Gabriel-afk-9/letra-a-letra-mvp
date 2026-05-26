package com.letraaletra.api.presentation.dto.response.websocket;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.presentation.dto.response.game.GameDTO;

@JsonTypeName("POSITIONS_UPDATED")
public record SwapPositionResponseDTO(
        GameDTO data
) implements WsResponseDTO {
}
