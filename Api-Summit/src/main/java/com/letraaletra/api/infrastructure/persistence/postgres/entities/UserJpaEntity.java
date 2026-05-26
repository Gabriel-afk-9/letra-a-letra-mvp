package com.letraaletra.api.infrastructure.persistence.postgres.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "\"user\"")
public class UserJpaEntity {
    @Id
    @Column(name = "user_id", nullable = false)
    private UUID id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "avatar_id")
    private String avatarId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "google_id")
    private String googleId;

    @Column(name = "can_change_nickname", nullable = false)
    private boolean canChangeNickname;
}
