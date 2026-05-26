package com.letraaletra.api.domain.game.player.actions;

import com.letraaletra.api.domain.game.event.Event;
import com.letraaletra.api.domain.game.state.GameState;
import com.letraaletra.api.domain.game.event.StateEvent;

import java.util.List;

public interface GameAction {
    List<Event> execute(GameState state, String userId);
}
