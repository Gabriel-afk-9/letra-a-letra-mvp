package com.letraaletra.api.domain.game.player.actions;

import com.letraaletra.api.domain.game.event.Event;
import com.letraaletra.api.domain.game.event.PlayerAreImmuneEvent;
import com.letraaletra.api.domain.game.event.PlayerBlindedEvent;
import com.letraaletra.api.domain.game.player.effect.ImmunityEffect;
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

public class BlindPlayerAction implements GameAction {
    private final String powerId;
    private final String target;

    public BlindPlayerAction(String powerId, String target) {
        this.powerId = powerId;
        this.target = target;
    }

    @Override
    public List<Event> execute(GameState state, String userId) {
        validatePlayerTurn(state, userId);

        Player player = state.getPlayerOrThrow(userId);
        validatePlayer(player);
        validatePower(player);

        player.resetPassedTurn();

        Player opponent = state.getPlayerOrThrow(target);
        validatePlayer(opponent);

        player.removeFromInventoryOrThrow(powerId);

        if (isImmune(opponent)) {
            return new ArrayList<>(List.of(new Event(
                    StateEvent.PLAYER_ARE_IMMUNE,
                    new PlayerAreImmuneEvent(target)
            )));
        }

        opponent.applyEffect(new BlindEffect());

        return new ArrayList<>(List.of(new Event(
                StateEvent.PLAYER_BLINDED,
                new PlayerBlindedEvent(target)
        )));
    }

    private void validatePlayerTurn(GameState state, String userId) {
        if (!state.currentPlayerTurn().equals(userId)) {
            throw new NotYourTurnException();
        }
    }

    private void validatePower(Player player) {
        PowerType power = player.getInventory().get(powerId);

        if (power != PowerType.BLIND) {
            throw new InvalidPlayerActionException();
        }
    }

    private void validatePlayer(Player player) {
        if (player == null) {
            throw new PlayerNotInGameException();
        }
    }

    private boolean isImmune(Player player) {
        return player.getEffects().stream()
                .anyMatch(effect -> effect instanceof ImmunityEffect);
    }
}
