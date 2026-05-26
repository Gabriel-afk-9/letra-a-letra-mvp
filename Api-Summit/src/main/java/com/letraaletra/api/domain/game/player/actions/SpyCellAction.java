package com.letraaletra.api.domain.game.player.actions;

import com.letraaletra.api.domain.game.event.Event;
import com.letraaletra.api.domain.game.event.PlayerSpiedEvent;
import com.letraaletra.api.domain.game.state.GameState;
import com.letraaletra.api.domain.game.event.StateEvent;
import com.letraaletra.api.domain.game.board.cell.Cell;
import com.letraaletra.api.domain.game.board.cell.PowerType;
import com.letraaletra.api.domain.game.board.cell.exception.CellAlreadyRevealedException;
import com.letraaletra.api.domain.game.board.position.Position;
import com.letraaletra.api.domain.game.player.Player;
import com.letraaletra.api.domain.game.player.effect.SpyEffect;
import com.letraaletra.api.domain.game.player.exception.InvalidPlayerActionException;
import com.letraaletra.api.domain.game.player.exception.NotYourTurnException;
import com.letraaletra.api.domain.game.player.exception.PlayerNotInGameException;

import java.util.ArrayList;
import java.util.List;

public class SpyCellAction implements GameAction {
    private final String powerId;
    private final Position position;

    public SpyCellAction(String powerId, Position position) {
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

        player.removeFromInventoryOrThrow(powerId);

        player.applyEffect(new SpyEffect(position));

        return new ArrayList<>(List.of(new Event(
                StateEvent.PLAYER_SPIED,
                new PlayerSpiedEvent(
                        userId
                )
        )));
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

        if (power != PowerType.SPY) {
            throw new InvalidPlayerActionException();
        }
    }

    private void validatePlayer(Player player) {
        if (player == null) {
            throw new PlayerNotInGameException();
        }
    }
}
