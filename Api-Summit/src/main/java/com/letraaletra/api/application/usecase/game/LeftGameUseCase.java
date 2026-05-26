package com.letraaletra.api.application.usecase.game;

import com.letraaletra.api.application.command.actor.LeftGameActorCommand;
import com.letraaletra.api.application.command.game.LeftGameCommand;
import com.letraaletra.api.application.output.actor.LeftGameResult;
import com.letraaletra.api.application.output.game.LeftGameOutput;
import com.letraaletra.api.application.port.Actor;
import com.letraaletra.api.application.port.ActorManager;
import com.letraaletra.api.application.usecase.UseCase;
import com.letraaletra.api.domain.game.GameStatus;
import com.letraaletra.api.domain.game.service.GameOverResult;
import com.letraaletra.api.domain.repository.game.GameRepository;
import com.letraaletra.api.domain.repository.user.UserRepository;
import com.letraaletra.api.domain.security.TokenService;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.domain.user.exceptions.UserNotFoundException;

import java.util.concurrent.CompletableFuture;

public class LeftGameUseCase implements UseCase<LeftGameCommand, LeftGameOutput> {
    private final TokenService tokenService;
    private final ActorManager<Game> actorManager;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;

    public LeftGameUseCase(TokenService tokenService, ActorManager<Game> actorManager, UserRepository userRepository, GameRepository gameRepository) {
        this.tokenService = tokenService;
        this.actorManager = actorManager;
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
    }

    public LeftGameOutput execute(LeftGameCommand command) {
        String gameId = tokenService.getTokenContent(command.token());
        Actor actor = actorManager.get(gameId);

        CompletableFuture<LeftGameResult> future = actor.enqueueCommand(new LeftGameActorCommand(command.session()));

        LeftGameResult result = future.join();

        User user = userRepository.find(result.user()).orElse(null);
        validateUser(user);

        user.leaveGame();
        userRepository.save(user);

        if (result.isEmpty()) {
            actorManager.remove(result.game().getId());

            result.game().setGameStatus(GameStatus.CLOSED);

            gameRepository.save(result.game());
        }

        return buildReturn(result.game(), command.token(), result.gameOverResult());
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new UserNotFoundException();
        }
    }

    private LeftGameOutput buildReturn(Game game, String token, GameOverResult gameOverResult) {
        return new LeftGameOutput(
                token,
                game,
                gameOverResult
        );
    }
}
