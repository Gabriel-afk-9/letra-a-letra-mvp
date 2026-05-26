package com.letraaletra.api.presentation.dto.response.http;

import com.letraaletra.api.presentation.dto.response.game.GameDTO;

public record FindByTokenResponseDTO(
        GameDTO data
) {
}
