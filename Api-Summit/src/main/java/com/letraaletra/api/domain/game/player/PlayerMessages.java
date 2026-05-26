package com.letraaletra.api.domain.game.player;

import com.letraaletra.api.domain.MessageCode;

public enum PlayerMessages implements MessageCode {
    PLAYER_NOT_FOUND("player_not_found"),
    PLAYER_ALREADY_EXISTS("player_already_exists"),
    PLAYER_NOT_IN_GAME("player_not_in_game"),

    INVALID_PLAYER_ACTION("invalid_player_action"),
    PLAYER_DISCONNECTED("player_disconnected");

    private final String message;

    PlayerMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}