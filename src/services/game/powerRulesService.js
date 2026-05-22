import { POWERS } from "../../config/powers.config.js";

export const PowerRulesService = {
    getConfig(powerType) {
        return POWERS[powerType] || null;
    },

    getScope(powerType) {
        return POWERS[powerType]?.scope || "CELL";
    },

    canUseWhileFrozen(powerType) {
        return !!POWERS[powerType]?.canUseWhileFrozen;
    },

    getIcon(powerType) {
        return POWERS[powerType]?.icon || "assets/powers/default.png";
    }
}; 