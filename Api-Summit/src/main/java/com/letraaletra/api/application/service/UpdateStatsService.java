package com.letraaletra.api.application.service;

import com.letraaletra.api.domain.game.player.Player;
import com.letraaletra.api.domain.repository.user.UserRepository;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.domain.user.exceptions.UserNotFoundException;

public class UpdateStatsService {
    private final UserRepository userRepository;

    public UpdateStatsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void execute(Player player, boolean isWinner) {
        User user = userRepository.find(player.getUserId())
                .orElseThrow(UserNotFoundException::new);

        user.registerMatchResult(isWinner);

        userRepository.save(user);
    }
}
