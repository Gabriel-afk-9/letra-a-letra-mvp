package com.letraaletra.api.application.command.player;

import com.letraaletra.api.domain.game.player.actions.GameAction;

public record PlayerActionCommand(
        String token,
        String user,
        GameAction action
) {
}
