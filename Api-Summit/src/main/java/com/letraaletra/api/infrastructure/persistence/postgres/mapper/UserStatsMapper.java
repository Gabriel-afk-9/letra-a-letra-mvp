package com.letraaletra.api.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.domain.user.stats.UserStats;
import com.letraaletra.api.infrastructure.persistence.postgres.entities.UserStatsJpaEntity;

import java.util.UUID;

public class UserStatsMapper {
    public static UserStats toDomain(UserStatsJpaEntity entity) {
        return new UserStats(
                entity.getTotalMatches(),
                entity.getTotalWins(),
                entity.getWinStreak(),
                entity.getPoints()
        );
    }

    public static UserStatsJpaEntity toEntity(UserStats domain, String userId) {
        UserStatsJpaEntity entity = new UserStatsJpaEntity();

        entity.setUserId(UUID.fromString(userId));
        entity.setTotalMatches(domain.getTotalMatchs());
        entity.setTotalWins(domain.getTotalWins());
        entity.setWinStreak(domain.getWinStreak());
        entity.setPoints(domain.getPoints());

        return entity;
    }
}
