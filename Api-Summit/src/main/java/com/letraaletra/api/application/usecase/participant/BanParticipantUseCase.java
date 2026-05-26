package com.letraaletra.api.application.usecase.participant;

import com.letraaletra.api.application.command.actor.BanParticipantActorCommand;
import com.letraaletra.api.application.command.participant.BanParticipantCommand;
import com.letraaletra.api.application.context.ModerationContext;
import com.letraaletra.api.application.context.ModerationContextFactory;
import com.letraaletra.api.application.output.participant.BanParticipantOutput;
import com.letraaletra.api.application.port.Actor;
import com.letraaletra.api.application.port.ActorManager;
import com.letraaletra.api.application.usecase.UseCase;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.repository.user.UserRepository;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.domain.user.exceptions.UserNotFoundException;

import java.util.concurrent.CompletableFuture;

public class BanParticipantUseCase implements UseCase<BanParticipantCommand, BanParticipantOutput> {
    private final ModerationContextFactory moderationContextFactory;
    private final UserRepository userRepository;
    private final ActorManager<Game> gameActorManager;

    public BanParticipantUseCase(ModerationContextFactory moderationContextFactory, UserRepository userRepository, ActorManager<Game> gameActorManager) {
        this.moderationContextFactory = moderationContextFactory;
        this.userRepository = userRepository;
        this.gameActorManager = gameActorManager;
    }

    public BanParticipantOutput execute(BanParticipantCommand command) {
        ModerationContext context = moderationContextFactory.resolve(command.token(), command.target(), command.user());

        Actor actor = gameActorManager.get(context.game().getId());
        CompletableFuture<Game> future = actor.enqueueCommand(new BanParticipantActorCommand(command.target(), command.user()));
        Game game = future.join();

        User user = userRepository.find(command.target()).orElse(null);
        validateUser(user);

        user.leaveGame();

        userRepository.save(user);

        return buildReturn(game, command.token());
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new UserNotFoundException();
        }
    }

    private BanParticipantOutput buildReturn(Game game, String token) {
        return new BanParticipantOutput(
                token,
                game
        );
    }
}
