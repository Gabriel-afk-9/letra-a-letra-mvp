package com.letraaletra.api.application.command.actor;

import com.letraaletra.api.application.output.actor.PlayerActionResult;
import com.letraaletra.api.application.port.GameTimeoutManager;
import com.letraaletra.api.application.port.TurnTimeoutManager;
import com.letraaletra.api.domain.game.*;
import com.letraaletra.api.domain.game.event.Event;
import com.letraaletra.api.domain.game.event.StateEvent;
import com.letraaletra.api.domain.game.event.TurnPassedEvent;
import com.letraaletra.api.domain.game.exception.GameNotRunningException;
import com.letraaletra.api.domain.game.exception.SpectatorCanNotPlayException;
import com.letraaletra.api.domain.game.participant.Participant;
import com.letraaletra.api.domain.game.participant.ParticipantRole;
import com.letraaletra.api.domain.game.player.Player;
import com.letraaletra.api.domain.game.player.actions.GameAction;
import com.letraaletra.api.domain.game.player.exception.PlayerNotInGameException;
import com.letraaletra.api.domain.game.service.GameOverResult;
import com.letraaletra.api.domain.game.state.GameState;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class PlayerActionActorCommand implements ActorCommand<PlayerActionResult> {
    private final String user;
    private final GameAction action;
    private final GameTimeoutManager gameTimeoutManager;
    private final TurnTimeoutManager turnTimeoutManager;

    public PlayerActionActorCommand(String user, GameAction action, GameTimeoutManager gameTimeoutManager, TurnTimeoutManager turnTimeoutManager) {
        this.user = user;
        this.action = action;
        this.gameTimeoutManager = gameTimeoutManager;
        this.turnTimeoutManager = turnTimeoutManager;
    }

    @Override
    public PlayerActionResult execute(Game game) {
        if (!(game.getGameStatus().equals(GameStatus.RUNNING))) {
            throw new GameNotRunningException();
        }

        validatePlayer(user, game);

        GameState state = game.getGameState();

        List<Event> events = action.execute(state, user);

        if (events == null) {
            events = new ArrayList<>();
        }

        GameOverResult gameOverResult = state.gameOverChecker();

        if (gameOverResult.finished()) {
            if (game.getGameType().equals(GameType.CUSTOM)) {
                game.setGameStatus(GameStatus.WAITING);
                gameTimeoutManager.start(game);

            } else {
                game.setGameStatus(GameStatus.CLOSED);
            }

            return new PlayerActionResult(events, gameOverResult, game);
        }

        Player current;
        do {
            state.nextTurn(Instant.now());

            current = state.getPlayerOrThrow(state.currentPlayerTurn());

            if (current.canNotPlay()) {
                events.add(new Event(StateEvent.TURN_PASSED, new TurnPassedEvent(current.getUserId())));
            }

        } while (current.canNotPlay());

        updateTurnEnds(state, events.size());

        turnTimeoutManager.start(game);

        return new PlayerActionResult(events, gameOverResult, game);
    }

    private void validatePlayer(String userId, Game game) {
        Participant participant = game.getParticipantByUserId(userId);

        if (participant == null) {
            throw new PlayerNotInGameException();
        }

        if (participant.getRole().equals(ParticipantRole.SPECTATOR)) {
            throw new SpectatorCanNotPlayException();
        }
    }

    private void updateTurnEnds(GameState state, int qtyEvents) {
        Instant finalTime = Instant.now().plusSeconds(45 + (qtyEvents * 2L));

        state.setTurnEndsAt(finalTime);
    }
}
