package com.letraaletra.api.domain.game.player.actions;

import com.letraaletra.api.domain.game.event.CellTrappedEvent;
import com.letraaletra.api.domain.game.event.Event;
import com.letraaletra.api.domain.game.state.GameState;
import com.letraaletra.api.domain.game.event.StateEvent;
import com.letraaletra.api.domain.game.board.cell.Cell;
import com.letraaletra.api.domain.game.board.cell.PowerType;
import com.letraaletra.api.domain.game.board.cell.effect.CellEffect;
import com.letraaletra.api.domain.game.board.cell.effect.InteractResult;
import com.letraaletra.api.domain.game.board.cell.effect.TrapEffect;
import com.letraaletra.api.domain.game.board.cell.exception.CellAlreadyRevealedException;
import com.letraaletra.api.domain.game.board.position.Position;
import com.letraaletra.api.domain.game.player.Player;
import com.letraaletra.api.domain.game.player.exception.InvalidPlayerActionException;
import com.letraaletra.api.domain.game.player.exception.NotYourTurnException;
import com.letraaletra.api.domain.game.player.exception.PlayerNotInGameException;

import java.util.ArrayList;
import java.util.List;

public class TrapCellAction implements GameAction {
    private final String powerId;
    private final Position position;

    public TrapCellAction(String powerId, Position position) {
        this.powerId = powerId;
        this.position = position;
    }

    @Override
    public List<Event> execute(GameState state, String userId) {
        validatePlayerTurn(state, userId);

        Player player = state.getPlayerOrThrow(userId);
        validatePlayer(player);
        validatePower(player);

        player.resetPassedTurn();

        Cell cell = state.getBoard().getCell(position);

        validateCell(cell);

        List<Event> events = new ArrayList<>();

        boolean canContinue = activateEffect(cell, userId, events);

        if (!canContinue) return null;

        cell.setEffect(
                new TrapEffect(userId)
        );

        events.add(new Event(
                StateEvent.CELL_TRAPPED,
                new CellTrappedEvent(
                        position,
                        userId
                )
        ));

        state.getPlayerOrThrow(userId).removeFromInventoryOrThrow(powerId);

        return events;
    }

    private void validatePlayerTurn(GameState state, String userId) {
        if (!state.currentPlayerTurn().equals(userId)) {
            throw new NotYourTurnException();
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

    private void validatePower(Player player) {
        PowerType power = player.getInventory().get(powerId);

        if (power != PowerType.TRAP) {
            throw new InvalidPlayerActionException();
        }
    }

    private void validatePlayer(Player player) {
        if (player == null) {
            throw new PlayerNotInGameException();
        }
    }

    private boolean activateEffect(Cell cell, String player, List<Event> events) {
        if (cell.hasEffect()) {
            CellEffect effect = cell.getEffect();

            if (!(effect instanceof TrapEffect))  return true;

            InteractResult result = effect.onInteract(this, player, cell);

            events.add(result.event());

            return result.canContinue();
        }

        return true;
    }
}
