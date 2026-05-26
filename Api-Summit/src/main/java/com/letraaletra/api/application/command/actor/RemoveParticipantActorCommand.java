package com.letraaletra.api.application.command.actor;

import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.exception.GameNotFoundException;
import com.letraaletra.api.domain.game.participant.Participant;
import com.letraaletra.api.domain.repository.user.UserRepository;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.domain.user.exceptions.UserNotFoundException;

import java.util.Optional;

public class RemoveParticipantActorCommand implements ActorCommand<Optional<Game>> {
    private final UserRepository userRepository;
    private final String userId;

    public RemoveParticipantActorCommand(
            UserRepository userRepository,
            String userId
    ) {
        this.userRepository = userRepository;
        this.userId = userId;
    }

    @Override
    public Optional<Game> execute(Game game) {
        User user = userRepository.find(userId).orElse(null);
        validateUser(user);

        validateGame(game);

        Participant participant = game.getParticipantByUserId(userId);

        if (participant == null || participant.isConnected()) return Optional.empty();

        user.leaveGame();
        game.remove(userId);

        userRepository.save(user);

        return Optional.of(game);
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new UserNotFoundException();
        }
    }

    private void validateGame(Game game) {
        if (game == null) {
            throw new GameNotFoundException();
        }
    }
}
