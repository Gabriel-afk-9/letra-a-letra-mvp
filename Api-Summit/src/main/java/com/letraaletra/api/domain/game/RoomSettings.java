package com.letraaletra.api.domain.game;

public class RoomSettings {
    private final boolean allowSpectators;
    private final boolean privateGame;

    public RoomSettings(boolean allowSpectators, boolean privateGame) {
        this.allowSpectators = allowSpectators;
        this.privateGame = privateGame;
    }

    public boolean roomAllowSpectators() {
        return allowSpectators;
    }

    public boolean isPrivateGame() {
        return privateGame;
    }
}
