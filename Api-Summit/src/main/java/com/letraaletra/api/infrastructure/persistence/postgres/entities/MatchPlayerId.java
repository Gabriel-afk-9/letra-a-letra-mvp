package com.letraaletra.api.infrastructure.persistence.postgres.entities;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@Getter
@Setter
public class MatchPlayerId implements Serializable {
    private UUID matchId;
    private UUID userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MatchPlayerId that)) return false;
        return Objects.equals(matchId, that.matchId) &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(matchId, userId);
    }
}
