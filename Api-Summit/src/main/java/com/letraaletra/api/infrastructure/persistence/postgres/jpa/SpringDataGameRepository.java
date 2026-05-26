package com.letraaletra.api.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.infrastructure.persistence.postgres.entities.GameJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataGameRepository extends JpaRepository<GameJpaEntity, UUID> {
    <S extends GameJpaEntity> S save(S entity);
}
