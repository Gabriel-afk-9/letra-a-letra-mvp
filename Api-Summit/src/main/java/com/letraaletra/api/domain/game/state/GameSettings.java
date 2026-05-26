package com.letraaletra.api.domain.game.state;

public class GameSettings {
    private final String themeId;
    private final GameMode gameMode;

    public GameSettings(String themeId, GameMode gameMode) {
        this.themeId = themeId;
        this.gameMode = gameMode;
    }

    public String getThemeId() {
        return themeId;
    }

    public GameMode getGameMode() {
        return gameMode;
    }
}
