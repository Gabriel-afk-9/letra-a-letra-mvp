package com.letraaletra.api.application.output.participant;

import com.letraaletra.api.domain.game.Game;

public record BanParticipantOutput(
        String token,
        Game game
) {
}
