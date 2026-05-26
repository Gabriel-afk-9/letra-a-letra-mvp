package com.letraaletra.api.domain.game.board.theme;

import com.letraaletra.api.domain.MessageCode;

public enum ThemeMessages implements MessageCode {
    FAILED_TO_LOAD_THEMES("failed_to_load_themes"),
    THEME_NOT_FOUND("theme_not_found");

    private final String message;

    ThemeMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
