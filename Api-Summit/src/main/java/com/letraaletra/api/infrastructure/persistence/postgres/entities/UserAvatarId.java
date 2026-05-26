package com.letraaletra.api.infrastructure.persistence.postgres.entities;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@Getter
@Setter
public class UserAvatarId implements Serializable {
    private UUID userId;
    private String avatarId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserAvatarId that)) return false;
        return Objects.equals(avatarId, that.avatarId) &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, avatarId);
    }
}
