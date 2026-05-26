package com.letraaletra.api.domain.repository.matchmaking;

import com.letraaletra.api.domain.game.matchmaking.MatchmakingUser;
import com.letraaletra.api.domain.game.state.GameMode;

public interface EnqueueUser {
    void add(MatchmakingUser user, GameMode gameMode);
}
