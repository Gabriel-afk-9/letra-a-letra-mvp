package com.letraaletra.api.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.domain.avatar.Avatar;
import com.letraaletra.api.infrastructure.persistence.postgres.entities.AvatarJpaEntity;

public class AvatarMapper {
    public static AvatarJpaEntity toEntity(Avatar avatar) {
        AvatarJpaEntity entity = new AvatarJpaEntity();

        entity.setId(avatar.avatarId());

        return entity;
    }

    public static Avatar toDomain(AvatarJpaEntity entity) {
        return new Avatar(
                entity.getId()
        );
    }
}
