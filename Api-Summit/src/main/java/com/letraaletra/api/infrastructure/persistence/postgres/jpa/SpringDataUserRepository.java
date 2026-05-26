package com.letraaletra.api.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.infrastructure.persistence.postgres.entities.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataUserRepository extends JpaRepository<UserJpaEntity, UUID> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<UserJpaEntity> findByEmail(String email);
    Optional<UserJpaEntity> findByGoogleId(String googleId);
    <S extends UserJpaEntity> S save(S entity);
}
