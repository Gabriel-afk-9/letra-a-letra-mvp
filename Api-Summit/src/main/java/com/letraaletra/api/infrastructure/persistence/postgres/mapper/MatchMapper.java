package com.letraaletra.api.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.domain.game.state.GameState;
import com.letraaletra.api.infrastructure.persistence.postgres.entities.MatchJpaEntity;

import java.time.LocalDateTime;
import java.util.UUID;

public class MatchMapper {
    public static MatchJpaEntity toEntity(GameState domain, String gameId, LocalDateTime endedAt) {
        if (domain == null) return null;

        MatchJpaEntity entity = new MatchJpaEntity();

        entity.setId(UUID.fromString(domain.getMatchId()));
        entity.setGameId(UUID.fromString(gameId));
        entity.setGameMode(domain.getBoard().gameMode());
        entity.setEndedAt(endedAt);

        return entity;
    }
}
