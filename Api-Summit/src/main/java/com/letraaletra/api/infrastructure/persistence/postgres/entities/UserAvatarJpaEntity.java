package com.letraaletra.api.infrastructure.persistence.postgres.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "\"user_avatars\"")
@Getter
@Setter
public class UserAvatarJpaEntity {
    @EmbeddedId
    private UserAvatarId userAvatarId;

    @Column(name = "unlocked_at", nullable = false)
    private LocalDateTime unlockedAt;
}
