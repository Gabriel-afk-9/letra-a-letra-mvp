package com.letraaletra.api.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.domain.game.player.Player;
import com.letraaletra.api.infrastructure.persistence.postgres.entities.MatchPlayerId;
import com.letraaletra.api.infrastructure.persistence.postgres.entities.MatchPlayersJpaEntity;

import java.util.UUID;

public class MatchPlayerMapper {
    public static MatchPlayersJpaEntity toEntity(Player domain, String matchId) {
        if (domain == null) return null;

        MatchPlayersJpaEntity entity = new MatchPlayersJpaEntity();

        MatchPlayerId id = new MatchPlayerId();

        id.setMatchId(UUID.fromString(matchId));
        id.setUserId(UUID.fromString(domain.getUserId()));

        entity.setMatchPlayerId(id);
        entity.setScore(domain.getScore());
        entity.setWinner(domain.getScore() == 3);

        return entity;
    }
}
