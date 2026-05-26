package com.letraaletra.api.infrastructure.persistence.postgres.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "\"match_players\"")
public class MatchPlayersJpaEntity {
    @EmbeddedId
    private MatchPlayerId matchPlayerId;

    @Column(name = "score")
    private int score;

    @Column(name = "is_winner")
    private boolean isWinner;
}
