package com.letraaletra.api.application.usecase.game;

import com.letraaletra.api.application.command.game.FindByTokenCommand;
import com.letraaletra.api.application.output.game.FindByTokenOutput;
import com.letraaletra.api.application.port.Actor;
import com.letraaletra.api.application.port.ActorManager;
import com.letraaletra.api.application.usecase.UseCase;
import com.letraaletra.api.domain.security.TokenService;
import com.letraaletra.api.domain.game.Game;

public class FindByTokenGameIdUseCase implements UseCase<FindByTokenCommand, FindByTokenOutput> {
    private final TokenService tokenService;
    private final ActorManager<Game> actorManager;

    public FindByTokenGameIdUseCase(TokenService tokenService, ActorManager<Game> actorManager) {
        this.tokenService = tokenService;
        this.actorManager = actorManager;
    }

    public FindByTokenOutput execute(FindByTokenCommand command) {
        String gameId = tokenService.getTokenContent(command.token());

        Actor actor = actorManager.get(gameId);

        return buildReturn(actor.getGame(), command.token());
    }

    private FindByTokenOutput buildReturn(Game game, String token) {
        return new FindByTokenOutput(
                token,
                game
        );
    }
}
