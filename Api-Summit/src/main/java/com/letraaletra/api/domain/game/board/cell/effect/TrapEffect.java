package com.letraaletra.api.domain.game.board.cell.effect;

import com.letraaletra.api.domain.game.event.Event;
import com.letraaletra.api.domain.game.event.StateEvent;
import com.letraaletra.api.domain.game.board.cell.Cell;
import com.letraaletra.api.domain.game.event.TrapTriggeredEvent;
import com.letraaletra.api.domain.game.player.actions.GameAction;
import com.letraaletra.api.domain.game.player.actions.RevealCellAction;
import com.letraaletra.api.domain.game.player.exception.InvalidPlayerActionException;

public class TrapEffect implements CellEffect {
    private final String ownerId;

    public TrapEffect(String ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public String getOwnerId() {
        return ownerId;
    }

    @Override
    public InteractResult onInteract(GameAction action, String player, Cell cell) {
        boolean isOwner = player.equals(ownerId);

        if (isOwner && !(action instanceof RevealCellAction)) {
            throw new InvalidPlayerActionException();
        }

        cell.clearEffect();

        return new InteractResult(isOwner, new Event(
                StateEvent.TRAP_TRIGGERED,
                new TrapTriggeredEvent(
                        cell.getPosition(),
                        player
                )
        ));
    }
}
