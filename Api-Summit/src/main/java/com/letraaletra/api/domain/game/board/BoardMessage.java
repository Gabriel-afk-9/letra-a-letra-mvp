package com.letraaletra.api.domain.game.board;

import com.letraaletra.api.domain.MessageCode;

public enum BoardMessage implements MessageCode {
    INVALID_CELL_POSITION("invalid_cell_position");

    private final String message;

    BoardMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
