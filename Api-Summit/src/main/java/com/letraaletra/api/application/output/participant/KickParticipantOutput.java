package com.letraaletra.api.application.output.participant;

import com.letraaletra.api.domain.game.Game;

public record KickParticipantOutput(
        String token,
        Game game
) {
}
