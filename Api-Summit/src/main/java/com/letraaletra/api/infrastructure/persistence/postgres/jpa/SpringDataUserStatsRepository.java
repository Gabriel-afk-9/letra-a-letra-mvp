package com.letraaletra.api.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.infrastructure.persistence.postgres.entities.UserStatsJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataUserStatsRepository extends JpaRepository<UserStatsJpaEntity, UUID> {
    <S extends UserStatsJpaEntity> S save(S entity);
}
