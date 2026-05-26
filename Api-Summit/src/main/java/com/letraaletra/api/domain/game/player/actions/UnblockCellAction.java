package com.letraaletra.api.domain.game.player.actions;

import com.letraaletra.api.domain.game.event.CellUnblockedEvent;
import com.letraaletra.api.domain.game.event.Event;
import com.letraaletra.api.domain.game.state.GameState;
import com.letraaletra.api.domain.game.event.StateEvent;
import com.letraaletra.api.domain.game.board.cell.Cell;
import com.letraaletra.api.domain.game.board.cell.PowerType;
import com.letraaletra.api.domain.game.board.cell.exception.CellAlreadyRevealedException;
import com.letraaletra.api.domain.game.board.position.Position;
import com.letraaletra.api.domain.game.player.Player;
import com.letraaletra.api.domain.game.player.exception.InvalidPlayerActionException;
import com.letraaletra.api.domain.game.player.exception.NotYourTurnException;

import java.util.ArrayList;
import java.util.List;

public class UnblockCellAction implements GameAction {
    private final String powerId;
    private final Position position;

    public UnblockCellAction(String powerId, Position position) {
        this.powerId = powerId;
        this.position = position;
    }

    @Override
    public List<Event> execute(GameState state, String userId) {
        validatePlayerTurn(state, userId);

        Cell cell = state.getBoard().getCell(position);
        validateCell(cell);

        Player player = state.getPlayerOrThrow(userId);
        validatePower(player);

        player.resetPassedTurn();

        player.removeFromInventoryOrThrow(powerId);

        cell.clearEffect();

        return new ArrayList<>(List.of(new Event(
                StateEvent.CELL_UNBLOCKED,
                new CellUnblockedEvent(
                        position,
                        userId
                )
        )));
    }

    private void validatePlayerTurn(GameState state, String userId) {
        if (!state.currentPlayerTurn().equals(userId)) {
            throw new NotYourTurnException();
        }
    }

    private void validatePower(Player player) {
        PowerType power = player.getInventory().get(powerId);

        if (power != PowerType.UNBLOCK) {
            throw new InvalidPlayerActionException();
        }
    }

    private void validateCell(Cell cell) {
        if (cell == null) {
            throw new InvalidPlayerActionException();
        }

        if (cell.isRevealed()) {
            throw new CellAlreadyRevealedException();
        }
    }
}
