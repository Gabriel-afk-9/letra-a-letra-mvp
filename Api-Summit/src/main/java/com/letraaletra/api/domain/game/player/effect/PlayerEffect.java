package com.letraaletra.api.domain.game.player.effect;

public interface PlayerEffect {
    int getDuration();
    void onTurnPassed();
    boolean canRemove();
}
