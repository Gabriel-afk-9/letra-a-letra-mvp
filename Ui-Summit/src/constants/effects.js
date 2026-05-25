export const EFFECT_EVENTS = new Set([
    "PLAYER_BLINDED", "PLAYER_USE_LANTERN",
    "PLAYER_FROZEN", "PLAYER_UNFREEZE",
    "PLAYER_USE_IMMUNITY",
    "IMMUNITY_APPLIED", "IMMUNITY_REMOVED",
    "TRAPS_DETECTED", "DETECT_TRAPS_REMOVED",
    "SPY_APPLIED", "SPY_REMOVED"
]);

export const EFFECT_MAP = {
    PLAYER_BLINDED:       { key: 'blind',         value: true  },
    PLAYER_USE_LANTERN:   { key: 'blind',         value: false },
    PLAYER_FROZEN:        { key: 'freeze',        value: true  },
    PLAYER_UNFREEZE:      { key: 'freeze',        value: false },
    IMMUNITY_APPLIED:     { key: 'immunity',      value: true  },
    IMMUNITY_REMOVED:     { key: 'immunity',      value: false },
    TRAPS_DETECTED:       { key: 'detect_traps',  value: true  },
    DETECT_TRAPS_REMOVED: { key: 'detect_traps',  value: false },
    SPY_APPLIED:          { key: 'spy',           value: true  },
    SPY_REMOVED:          { key: 'spy',           value: false },
};