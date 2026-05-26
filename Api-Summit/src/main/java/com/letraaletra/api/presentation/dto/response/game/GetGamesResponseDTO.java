package com.letraaletra.api.presentation.dto.response.game;

import java.util.List;

public record GetGamesResponseDTO(
        List<GameDTO> games
) {
}
