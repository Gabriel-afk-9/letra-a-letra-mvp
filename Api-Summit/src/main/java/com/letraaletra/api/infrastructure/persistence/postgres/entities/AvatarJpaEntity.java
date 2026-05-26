package com.letraaletra.api.infrastructure.persistence.postgres.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "\"avatar\"")
@Getter
@Setter
public class AvatarJpaEntity {
    @Id
    @Column(name = "avatar_id", nullable = false)
    private String id;
}