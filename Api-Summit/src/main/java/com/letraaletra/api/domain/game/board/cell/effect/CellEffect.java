package com.letraaletra.api.domain.game.board.cell.effect;

import com.letraaletra.api.domain.game.board.cell.Cell;
import com.letraaletra.api.domain.game.player.actions.GameAction;

public interface CellEffect {
    String getOwnerId();
    InteractResult onInteract(GameAction action, String player, Cell cell);
}
