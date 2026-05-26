package com.letraaletra.api.domain.game.player.effect;

import com.letraaletra.api.domain.game.board.position.Position;

public class SpyEffect implements PlayerEffect {
    private int duration;
    private final Position position;

    public SpyEffect(Position position) {
        this.duration = 6;
        this.position = position;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    public Position getPosition() {
        return position;
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
