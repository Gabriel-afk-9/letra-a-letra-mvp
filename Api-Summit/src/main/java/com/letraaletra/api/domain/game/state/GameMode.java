package com.letraaletra.api.domain.game.state;

import com.letraaletra.api.domain.game.board.cell.PowerRarity;

import java.util.Map;

public enum GameMode {
    EASY(
            new int[][]{{0, 1}, {1, 0}},
            0.2,
            Map.of(PowerRarity.COMMON, 0.6, PowerRarity.RARE, 0.25, PowerRarity.EPIC, 0.1, PowerRarity.LEGENDARY, 0.05)
    ),
    NORMAL(
            new int[][]{{0, 1}, {0, -1}, {1, 0}, {-1, 0}},
            0.25,
            Map.of(PowerRarity.COMMON, 0.55, PowerRarity.RARE, 0.25, PowerRarity.EPIC, 0.13, PowerRarity.LEGENDARY, 0.07)
    ),
    HARD(
            new int[][]{{0, 1}, {1, 0}, {1, 1}, {-1, 0}, {0, -1}, {1, -1}},
            0.3,
            Map.of(PowerRarity.COMMON, 0.45, PowerRarity.RARE, 0.3, PowerRarity.EPIC, 0.17, PowerRarity.LEGENDARY, 0.08)
    ),
    INSANE(
            new int[][]{{0, 1}, {1, 0}, {1, 1}, {-1, 0}, {0, -1}, {-1, -1}, {1, -1}, {-1, 1}},
            0.5,
            Map.of(PowerRarity.COMMON, 0.35, PowerRarity.RARE, 0.3, PowerRarity.EPIC, 0.22, PowerRarity.LEGENDARY, 0.13)
    ),
    CATACLYSM(
            new int[][]{{0, 1}, {1, 0}, {1, 1}, {-1, 0}, {0, -1}, {-1, -1}, {1, -1}, {-1, 1}},
            1,
            Map.of(PowerRarity.COMMON, 0.03, PowerRarity.RARE, 0.47, PowerRarity.EPIC, 0.3, PowerRarity.LEGENDARY, 0.0)
    );

    private final int[][] directions;
    private final double chancePerCell;
    private final Map<PowerRarity, Double> percentages;

    GameMode(int[][] directions, double chancePerCell, Map<PowerRarity, Double> percentages) {
        this.directions = directions;
        this.chancePerCell = chancePerCell;
        this.percentages = percentages;
    }

    public int[][] getDirections() {
        return directions;
    }

    public double getChancePerCell() {
        return chancePerCell;
    }

    public Map<PowerRarity, Double> getPercentages() {
        return percentages;
    }
}