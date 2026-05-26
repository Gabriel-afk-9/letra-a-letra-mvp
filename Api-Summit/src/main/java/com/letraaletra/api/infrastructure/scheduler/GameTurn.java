package com.letraaletra.api.infrastructure.scheduler;

import org.jspecify.annotations.NonNull;

import java.time.Instant;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class GameTurn implements Delayed {
    private final String gameId;
    private final Instant turnEndsAt;
    private final int version;

    public GameTurn(String gameId, Instant turnEndsAt, int version) {
        this.gameId = gameId;
        this.turnEndsAt = turnEndsAt;
        this.version = version;
    }

    public String gameId() { return gameId; }
    public int version() { return version; }

    @Override
    public long getDelay(TimeUnit unit) {
        long diff = turnEndsAt.toEpochMilli() - Instant.now().toEpochMilli();
        return unit.convert(diff, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(@NonNull Delayed other) {
        return Long.compare(
                this.turnEndsAt.toEpochMilli(),
                ((GameTurn) other).turnEndsAt.toEpochMilli()
        );
    }
}
