package com.letraaletra.api.domain.game.player.actions;

import com.letraaletra.api.domain.game.event.Event;
import com.letraaletra.api.domain.game.event.PlayerUseLanternEvent;
import com.letraaletra.api.domain.game.state.GameState;
import com.letraaletra.api.domain.game.event.StateEvent;
import com.letraaletra.api.domain.game.board.cell.PowerType;
import com.letraaletra.api.domain.game.player.Player;
import com.letraaletra.api.domain.game.player.effect.BlindEffect;
import com.letraaletra.api.domain.game.player.exception.InvalidPlayerActionException;
import com.letraaletra.api.domain.game.player.exception.NotYourTurnException;
import com.letraaletra.api.domain.game.player.exception.PlayerNotInGameException;

import java.util.ArrayList;
import java.util.List;

public class LanternAction implements GameAction {
    private final String powerId;

    public LanternAction(String powerId) {
        this.powerId = powerId;
    }

    @Override
    public List<Event> execute(GameState state, String userId) {
        validatePlayerTurn(state, userId);

        Player player = state.getPlayerOrThrow(userId);
        validatePlayer(player);
        validatePower(player);

        player.resetPassedTurn();

        player.removeFromInventoryOrThrow(powerId);

        player.removeEffect(BlindEffect.class);

        return new ArrayList<>(List.of(new Event(
                StateEvent.PLAYER_USE_LANTERN,
                new PlayerUseLanternEvent(userId)
        )));
    }

    private void validatePlayerTurn(GameState state, String userId) {
        if (!state.currentPlayerTurn().equals(userId)) {
            throw new NotYourTurnException();
        }
    }

    private void validatePower(Player player) {
        PowerType power = player.getInventory().get(powerId);

        if (power != PowerType.LANTERN) {
            throw new InvalidPlayerActionException();
        }
    }

    private void validatePlayer(Player player) {
        if (player == null) {
            throw new PlayerNotInGameException();
        }
    }
}
