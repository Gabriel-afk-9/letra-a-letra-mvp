package com.letraaletra.api.domain.game;

import com.letraaletra.api.domain.MessageCode;

public enum GameMessages implements MessageCode {
    GAME_FOUND("game_found"),
    GAMES_FOUND("games_found"),
    GAME_NOT_FOUND("game_not_found"),
    GAME_ALREADY_STARTED("game_already_started"),
    GAME_ALREADY_FINISHED("game_already_finished"),
    GAME_NOT_STARTED("game_not_started"),
    ONLY_HOST_CAN_START("only_host_can_start"),
    ONLY_HOST_CAN_MODERATE("only_host_can_moderate"),
    INVALID_MODERATE_ACTION("invalid_moderate_action"),

    GAME_IS_RUNNING("game_is_running"),
    GAME_NOT_RUNNING("game_not_running"),
    INSUFFICIENT_PLAYERS("insufficient_players"),
    ROOM_FULL("room_full"),

    PARTICIPANT_ALREADY_BANNED("participant_already_banned"),
    PARTICIPANT_NOT_BANNED("participant_not_banned"),

    NOT_YOUR_TURN("not_your_turn"),
    INVALID_MOVE("invalid_move"),
    CELL_ALREADY_REVEALED("cell_already_revealed"),
    CELL_ALREADY_HAS_AN_EFFECT("cell_already_has_an_effect"),
    INVALID_POSITION("invalid_position"),
    SPECTATOR_CAN_NOT_PLAY("spectator_can_not_play"),

    WORD_ALREADY_FOUND("word_already_found"),

    MAX_PLAYERS_REACHED("max_players_reached");

    private final String message;

    GameMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}