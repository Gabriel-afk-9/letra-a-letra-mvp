package com.letraaletra.api.application.usecase.player;

import com.letraaletra.api.application.command.actor.PlayerActionActorCommand;
import com.letraaletra.api.application.command.player.PlayerActionCommand;
import com.letraaletra.api.application.output.actor.PlayerActionResult;
import com.letraaletra.api.application.output.player.PlayerActionOutput;
import com.letraaletra.api.application.port.Actor;
import com.letraaletra.api.application.port.ActorManager;
import com.letraaletra.api.application.port.GameTimeoutManager;
import com.letraaletra.api.application.port.TurnTimeoutManager;
import com.letraaletra.api.application.service.GameOverHandler;
import com.letraaletra.api.application.usecase.UseCase;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.event.Event;
import com.letraaletra.api.domain.game.service.GameOverResult;
import com.letraaletra.api.domain.security.TokenService;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class PlayerActionUseCase implements UseCase<PlayerActionCommand, PlayerActionOutput> {
    private final TokenService tokenService;
    private final GameTimeoutManager gameTimeoutManager;
    private final TurnTimeoutManager turnTimeoutManager;
    private final ActorManager<Game> gameActorManager;
    private final GameOverHandler gameOverHandler;

    public PlayerActionUseCase(
            TokenService tokenService,
            GameTimeoutManager gameTimeoutManager,
            TurnTimeoutManager turnTimeoutManager,
            ActorManager<Game> gameActorManager,
            GameOverHandler gameOverHandler
    ) {
        this.tokenService = tokenService;
        this.gameTimeoutManager = gameTimeoutManager;
        this.turnTimeoutManager = turnTimeoutManager;
        this.gameActorManager = gameActorManager;
        this.gameOverHandler = gameOverHandler;
    }

    public PlayerActionOutput execute(PlayerActionCommand command) {
        String gameId = tokenService.getTokenContent(command.token());

        Actor actor = gameActorManager.get(gameId);

        CompletableFuture<PlayerActionResult> future = actor.enqueueCommand(new PlayerActionActorCommand(
                command.user(), command.action(), gameTimeoutManager, turnTimeoutManager
        ));

        PlayerActionResult result = future.join();

        gameOverHandler.handle(result.game(), result.gameOverResult());

        return buildOutput(result.game(), result.gameOverResult(), result.events());
    }

    private PlayerActionOutput buildOutput(Game game, GameOverResult gameOverResult, List<Event> events) {
        return new PlayerActionOutput(
                game,
                events,
                gameOverResult.finished() ? Optional.of(gameOverResult) : Optional.empty()
        );
    }
}
