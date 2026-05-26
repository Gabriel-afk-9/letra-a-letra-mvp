package com.letraaletra.api.application.command.game;

import com.letraaletra.api.domain.game.state.GameMode;
import com.letraaletra.api.domain.game.matchmaking.MatchmakingUser;

public record JoinMatchmakingCommand(
    MatchmakingUser matchmakingUser,
    GameMode gameMode
) {
}
