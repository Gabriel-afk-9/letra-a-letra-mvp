package com.letraaletra.api.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.infrastructure.persistence.postgres.entities.MatchJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataMatchRepository extends JpaRepository<MatchJpaEntity, UUID> {
    <S extends MatchJpaEntity> S save(S entity);
}
