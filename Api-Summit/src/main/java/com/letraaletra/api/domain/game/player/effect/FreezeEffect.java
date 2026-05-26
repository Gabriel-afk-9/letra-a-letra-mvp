package com.letraaletra.api.domain.game.player.effect;

public class FreezeEffect implements PlayerEffect {
    private int duration;

    public FreezeEffect() {
        this.duration = 6;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public void onTurnPassed() {
        duration = Math.max(0, duration - 1);
    }

    @Override
    public boolean canRemove() {
        return duration == 0;
    }
}
