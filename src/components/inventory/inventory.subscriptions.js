import { store } from "../../state/store.js";
import { PowerRulesService } from "../../services/game/powerRulesService.js";

export function registerInventorySubscriptions(component) {
    store.subscribe('players', () => component.render());

    store.subscribe('activePower', () => component.render());

    store.subscribe('playerEffects', () => {
        const activeType = store.state.activePower?.type;
        if (component.isFrozen() && activeType && !PowerRulesService.canUseWhileFrozen(activeType)) {
            component.clearSelection();
        }
        component.render();
    });
}