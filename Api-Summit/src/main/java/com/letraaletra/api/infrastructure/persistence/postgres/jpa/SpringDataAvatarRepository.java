package com.letraaletra.api.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.infrastructure.persistence.postgres.entities.AvatarJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataAvatarRepository extends JpaRepository<AvatarJpaEntity, String> {
    <S extends AvatarJpaEntity> S save(S entity);
}