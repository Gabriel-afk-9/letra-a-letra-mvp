package com.letraaletra.api.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.infrastructure.persistence.postgres.entities.UserJpaEntity;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.infrastructure.persistence.postgres.entities.UserStatsJpaEntity;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserMapper {
    public static User toDomain(UserJpaEntity entity, UserStatsJpaEntity statsJpa) {
        if (entity == null) return null;

        return new User(
                entity.getId().toString(),
                entity.getUsername(),
                entity.getAvatarId(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getGoogleId(),
                entity.isCanChangeNickname(),
                UserStatsMapper.toDomain(statsJpa)
        );
    }

    public static UserJpaEntity toEntity(User user) {
        if (user == null) return null;

        UserJpaEntity entity = new UserJpaEntity();
        entity.setId(UUID.fromString(user.getId()));
        entity.setUsername(user.getNickname());
        entity.setEmail(user.getEmail());
        entity.setPasswordHash(user.getHashPassword());
        entity.setGoogleId(user.getGoogleId());
        entity.setAvatarId(user.getAvatar());
        entity.setCanChangeNickname(user.canChangeNickname());
        entity.setCreatedAt(LocalDateTime.now());

        return entity;
    }
}
