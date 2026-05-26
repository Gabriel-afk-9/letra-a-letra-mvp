package com.letraaletra.api.domain.game.board.cell;

public enum PowerType {
    BLOCK(PowerRarity.COMMON),
    UNBLOCK(PowerRarity.COMMON),
    TRAP(PowerRarity.COMMON),
    DETECT_TRAPS(PowerRarity.COMMON),
    SPY(PowerRarity.RARE),
    FREEZE(PowerRarity.RARE),
    UNFREEZE(PowerRarity.RARE),
    BLIND(PowerRarity.EPIC),
    LANTERN(PowerRarity.EPIC),
    IMMUNITY(PowerRarity.LEGENDARY);

    private final PowerRarity powerRarity;

    PowerType(PowerRarity powerRarity) {
        this.powerRarity = powerRarity;
    }

    public PowerRarity getPowerRarity() {
        return powerRarity;
    }
}
