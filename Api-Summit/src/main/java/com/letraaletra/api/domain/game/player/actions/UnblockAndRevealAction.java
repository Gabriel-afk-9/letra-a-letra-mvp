package com.letraaletra.api.domain.game.player.actions;

import com.letraaletra.api.domain.game.event.Event;
import com.letraaletra.api.domain.game.state.GameState;

import java.util.List;
import java.util.stream.Stream;

public class UnblockAndRevealAction implements GameAction {
    private final UnblockCellAction unblock;
    private final RevealCellAction reveal;

    public UnblockAndRevealAction(UnblockCellAction unblock, RevealCellAction reveal) {
        this.unblock = unblock;
        this.reveal = reveal;
    }

    @Override
    public List<Event> execute(GameState state, String userId) {
        List<Event> unblockEvents = unblock.execute(state, userId);

        List<Event> revealEvents = reveal.execute(state, userId);

        return Stream.concat(
                unblockEvents.stream(),
                revealEvents.stream()
        ).toList();
    }
}
