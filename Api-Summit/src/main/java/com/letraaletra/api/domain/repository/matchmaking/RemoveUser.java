package com.letraaletra.api.domain.repository.matchmaking;

import com.letraaletra.api.domain.game.matchmaking.MatchmakingUser;

public interface RemoveUser {
    void remove(MatchmakingUser user);
    void removeById(String id);
}
