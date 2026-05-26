package com.letraaletra.api.application.usecase.game;

import com.letraaletra.api.application.command.actor.ExpireTurnActorCommand;
import com.letraaletra.api.application.command.game.ExpireTurnCommand;
import com.letraaletra.api.application.output.actor.ExpireTurnResult;
import com.letraaletra.api.application.output.game.ExpireTurnOutput;
import com.letraaletra.api.application.port.Actor;
import com.letraaletra.api.application.port.ActorManager;
import com.letraaletra.api.application.service.GameOverHandler;
import com.letraaletra.api.application.usecase.UseCase;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.repository.user.UserRepository;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.domain.user.exceptions.UserNotFoundException;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ExpireTurnUseCase implements UseCase<ExpireTurnCommand, Optional<ExpireTurnOutput>> {
    private final ActorManager<Game> gameActorManager;
    private final GameOverHandler gameOverHandler;
    private final UserRepository userRepository;

    public ExpireTurnUseCase(
            ActorManager<Game> gameActorManager,
            GameOverHandler gameOverHandler,
            UserRepository userRepository
    ) {
        this.gameActorManager = gameActorManager;
        this.gameOverHandler = gameOverHandler;
        this.userRepository = userRepository;
    }

    public Optional<ExpireTurnOutput> execute(ExpireTurnCommand command) {
        Actor actor = gameActorManager.get(command.gameId());

        CompletableFuture<Optional<ExpireTurnResult>> future = actor.enqueueCommand(
                new ExpireTurnActorCommand(command.version())
        );

        Optional<ExpireTurnResult> result = future.join();

        removeUserRoomId(result.orElse(null));

        return result.flatMap(this::buildOutput);
    }

    private void removeUserRoomId(ExpireTurnResult expireTurn) {
        if (expireTurn == null) return;

        if (expireTurn.gameOverResult().finished()) {
            gameOverHandler.handle(expireTurn.game(), expireTurn.gameOverResult());
        }

        if (expireTurn.removedBecauseAfk()) {
            handleAfkRemoval(expireTurn);
        }
    }

    private Optional<ExpireTurnOutput> buildOutput(ExpireTurnResult result) {
        return Optional.of(
                new ExpireTurnOutput(
                        "TURN_EXPIRED",
                        result.whoPassed(),
                        result.game().getGameState().currentPlayerTurn(),
                        result.game(),
                        result.gameOverResult(),
                        result.removedBecauseAfk()
                )
        );
    }

    private User getUserOrThrow(String userId) {
        return userRepository.find(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    private void handleAfkRemoval(ExpireTurnResult expireTurn) {
        User user = getUserOrThrow(expireTurn.whoPassed());

        user.leaveGame();
        userRepository.save(user);
    }
}
