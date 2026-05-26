package com.letraaletra.api.presentation.dto.response.game;

import com.letraaletra.api.presentation.dto.response.player.PlayerDTO;

public record GameOverDTO(
        PlayerDTO winner,
        PlayerDTO loser
) {
}
