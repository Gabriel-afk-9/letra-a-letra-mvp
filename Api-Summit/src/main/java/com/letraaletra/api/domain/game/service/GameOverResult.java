package com.letraaletra.api.domain.game.service;

import com.letraaletra.api.domain.game.player.Player;

public record GameOverResult(
        boolean finished,
        Player winner,
        Player loser
) {}