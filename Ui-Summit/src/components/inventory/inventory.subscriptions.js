import { store } from "../../state/store.js";
import { PowerRulesService } from "../../services/game/powerRulesService.js";

export function registerInventorySubscriptions(component) {
    const unsubscribes = [];

    unsubscribes.push(store.subscribe('activePower', () => component.render()));

    unsubscribes.push(store.subscribe('playerEffects', () => {
        const activeType = store.state.activePower?.type;
        if (component.isFrozen() && activeType && !PowerRulesService.canUseWhileFrozen(activeType)) {
            component.clearSelection();
        }
        component.render();
    }));

    return unsubscribes;
}
