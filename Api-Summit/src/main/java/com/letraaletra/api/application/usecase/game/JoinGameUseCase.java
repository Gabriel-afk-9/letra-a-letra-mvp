package com.letraaletra.api.application.usecase.game;

import com.letraaletra.api.application.command.actor.JoinGameActorCommand;
import com.letraaletra.api.application.command.game.JoinGameCommand;
import com.letraaletra.api.application.output.game.JoinGameOutput;
import com.letraaletra.api.application.port.Actor;
import com.letraaletra.api.application.port.ActorManager;
import com.letraaletra.api.application.usecase.UseCase;
import com.letraaletra.api.domain.security.TokenService;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.repository.user.UserRepository;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.domain.user.exceptions.UserNotFoundException;

import java.util.concurrent.CompletableFuture;

public class JoinGameUseCase implements UseCase<JoinGameCommand, JoinGameOutput> {
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final ActorManager<Game> actorManager;

    public JoinGameUseCase(UserRepository userRepository, TokenService tokenService, ActorManager<Game> actorManager) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.actorManager = actorManager;
    }

    public JoinGameOutput execute(JoinGameCommand command) {
        String gameId = tokenService.getTokenContent(command.token());
        User user = userRepository.find(command.user()).orElse(null);
        validateUser(user);

        Actor actor = actorManager.get(gameId);
        CompletableFuture<Game> future = actor.enqueueCommand(new JoinGameActorCommand(user, command.session()));

        Game game = future.join();
        userRepository.save(user);

        return buildReturn(command.token(), game);
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new UserNotFoundException();
        }
    }

    private JoinGameOutput buildReturn(String token, Game game) {
        return new JoinGameOutput(token, game);
    }
}
