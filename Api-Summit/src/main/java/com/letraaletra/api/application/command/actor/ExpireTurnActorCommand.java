package com.letraaletra.api.application.command.actor;

import com.letraaletra.api.application.output.actor.ExpireTurnResult;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.state.GameState;
import com.letraaletra.api.domain.game.GameStatus;
import com.letraaletra.api.domain.game.player.Player;
import com.letraaletra.api.domain.game.service.GameOverResult;

import java.time.Instant;
import java.util.Optional;

public class ExpireTurnActorCommand implements ActorCommand<Optional<ExpireTurnResult>> {
    private final int version;

    public ExpireTurnActorCommand(
            int version
    ) {
        this.version = version;
    }

    @Override
    public Optional<ExpireTurnResult> execute(Game game) {
        if (game == null || game.getGameStatus() == GameStatus.WAITING) {
            return Optional.empty();
        }

        GameState state = game.getGameState();

        if (state.getVersion() != version) {
            return Optional.empty();
        }

        Instant now = Instant.now();

        if (!state.isTurnExpired(now)) {
            return Optional.empty();
        }

        String whoPassed = state.currentPlayerTurn();

        Player player = state.getPlayerOrThrow(whoPassed);
        player.passedTurn();

        boolean shouldRemove = player.getPassedTurn() >= 3;

        if (shouldRemove) {
            game.remove(whoPassed);
        } else {
            state.nextTurn(now.plusSeconds(45));
        }

        GameOverResult gameOverResult = state.gameOverChecker();

        return Optional.of(new ExpireTurnResult(
                whoPassed,
                game,
                gameOverResult,
                shouldRemove
        ));
    }
}
