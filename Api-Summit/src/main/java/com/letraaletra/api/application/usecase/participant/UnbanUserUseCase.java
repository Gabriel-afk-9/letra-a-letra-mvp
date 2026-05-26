package com.letraaletra.api.application.usecase.participant;

import com.letraaletra.api.application.command.actor.UnbanParticipantActorCommand;
import com.letraaletra.api.application.command.participant.UnbanParticipantCommand;
import com.letraaletra.api.application.output.participant.UnbanParticipantOutput;
import com.letraaletra.api.application.port.Actor;
import com.letraaletra.api.application.port.ActorManager;
import com.letraaletra.api.application.usecase.UseCase;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.security.TokenService;

import java.util.concurrent.CompletableFuture;

public class UnbanUserUseCase implements UseCase<UnbanParticipantCommand, UnbanParticipantOutput> {
    private final TokenService tokenService;
    private final ActorManager<Game> gameActorManager;

    public UnbanUserUseCase(TokenService tokenService, ActorManager<Game> gameActorManager) {
        this.tokenService = tokenService;
        this.gameActorManager = gameActorManager;
    }

    public UnbanParticipantOutput execute(UnbanParticipantCommand command) {
        String gameId = tokenService.getTokenContent(command.token());

        Actor actor = gameActorManager.get(gameId);

        CompletableFuture<Game> future = actor.enqueueCommand(new UnbanParticipantActorCommand(command.target(), command.user()));
        Game game = future.join();

        return buildReturn(game);
    }

    private UnbanParticipantOutput buildReturn(Game game) {
        return new UnbanParticipantOutput(game);
    }
}
