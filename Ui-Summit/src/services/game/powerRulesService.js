import { POWERS_CONFIG } from "../../config/powers.config.js";

export const PowerRulesService = {
    getConfig(powerType) {
        return POWERS_CONFIG[powerType] || null;
    },

    getScope(powerType) {
        return POWERS_CONFIG[powerType]?.scope || "CELL";
    },

    canUseWhileFrozen(powerType) {
        return !!POWERS_CONFIG[powerType]?.isFreezeRecovery;
    },

    getIcon(powerType) {
        return POWERS_CONFIG[powerType]?.icon || "assets/powers/default.png";
    }
}; 