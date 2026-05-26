package com.letraaletra.api.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.infrastructure.persistence.postgres.entities.AvatarJpaEntity;
import com.letraaletra.api.infrastructure.persistence.postgres.entities.UserAvatarId;
import com.letraaletra.api.infrastructure.persistence.postgres.entities.UserAvatarJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataUserAvatarRepository extends JpaRepository<UserAvatarJpaEntity, UserAvatarId> {
    <S extends UserAvatarJpaEntity> S save(S entity);
    List<AvatarJpaEntity> findByUserAvatarId_UserId(UUID userId);
}
