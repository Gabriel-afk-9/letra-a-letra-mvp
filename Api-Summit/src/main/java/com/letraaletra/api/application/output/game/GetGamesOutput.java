package com.letraaletra.api.application.output.game;

import com.letraaletra.api.domain.game.Game;

import java.util.List;
import java.util.Map;

public record GetGamesOutput(
        List<Game> games,
        Map<String, String> tokens
) {
}
