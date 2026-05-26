package com.letraaletra.api.application.usecase.participant;

import com.letraaletra.api.application.command.participant.ReconnectParticipantCommand;
import com.letraaletra.api.application.output.participant.ReconnectParticipantOutput;
import com.letraaletra.api.application.port.Actor;
import com.letraaletra.api.application.port.ActorManager;
import com.letraaletra.api.application.port.DisconnectScheduler;
import com.letraaletra.api.application.usecase.UseCase;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.participant.Participant;
import com.letraaletra.api.domain.repository.user.UserRepository;
import com.letraaletra.api.domain.user.User;

import java.util.Optional;

public class ReconnectUseCase implements UseCase<ReconnectParticipantCommand, Optional<ReconnectParticipantOutput>> {
    private final ActorManager<Game> actorManager;
    private final DisconnectScheduler disconnectScheduler;
    private final UserRepository userRepository;

    public ReconnectUseCase(
            ActorManager<Game> actorManager,
            DisconnectScheduler disconnectScheduler,
            UserRepository userRepository
    ) {
        this.actorManager = actorManager;
        this.disconnectScheduler = disconnectScheduler;
        this.userRepository = userRepository;
    }

    public Optional<ReconnectParticipantOutput> execute(ReconnectParticipantCommand command) {
        String userId = command.user();
        if (userId == null) return Optional.empty();

        User user = userRepository.find(userId).orElse(null);
        if (user == null) return Optional.empty();

        if (user.isNotInGame()) {
            return Optional.empty();
        }

        try {
            Actor actor = actorManager.get(user.getCurrentGameId());

            Game game = actor.getGame();

            Participant participant = game.getParticipantByUserId(userId);
            if (participant == null) return Optional.empty();

            disconnectScheduler.cancel(userId, game.getId());

            game.reconnect(userId, command.session());

            return buildReturn(game);

        } catch (Exception e) {
            user.leaveGame();
            userRepository.save(user);
            return Optional.empty();
        }
    }

    private Optional<ReconnectParticipantOutput> buildReturn(Game game) {
        return Optional.of(new ReconnectParticipantOutput(
                game
        ));
    }
}
