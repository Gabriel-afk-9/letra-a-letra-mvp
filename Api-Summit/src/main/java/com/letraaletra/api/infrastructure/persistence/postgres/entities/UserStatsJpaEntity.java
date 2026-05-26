package com.letraaletra.api.infrastructure.persistence.postgres.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "\"user_stats\"")
public class UserStatsJpaEntity {
    @Id
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "total_matches")
    private int totalMatches;

    @Column(name = "total_wins")
    private int totalWins;

    @Column(name = "win_streak")
    private int winStreak;

    @Column(name = "points")
    private int points;
}
