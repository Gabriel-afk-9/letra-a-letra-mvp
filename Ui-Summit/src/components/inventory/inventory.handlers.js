import { store } from "../../state/store.js";
import { GameService } from "../../services/game/gameService.js";
import { PowerRulesService } from "../../services/game/powerRulesService.js";
import { UiModeService } from "../../services/ui/uiModeService.js";

export function handleCardClick(component, card) {
    const { id, type, scope } = card.dataset;

    if (component.isFrozen() && !PowerRulesService.canUseWhileFrozen(type)) {
        store.state.notification = { message: "🥶 Você está congelado!", type: "opponent" };
        return;
    }

    if (store.state.activePower?.id === id) {
        component.clearSelection();
        return;
    }

    store.state.activePower = { id, type, scope };
    UiModeService.setTargetMode(scope);
}

export function handleSwipe(component, powerId, powerType, scope, diffY) {
    if (component.isFrozen() && !PowerRulesService.canUseWhileFrozen(powerType)) return;

    if (diffY > 50) {
        if (scope === "GLOBAL") {
            GameService.playGlobalPower(powerId, powerType);
            component.clearSelection();
        } else {
            store.state.notification = { message: "Selecione uma célula no tabuleiro!", type: "me" };
        }
    } else if (diffY < -50) {
        GameService.discardPower(powerId);
        component.clearSelection();
    }
}

export function handleUseBtn(component, activePower) {
    if (component.isFrozen() && !PowerRulesService.canUseWhileFrozen(activePower.type)) {
        store.state.notification = {
            message: "🥶 Você está congelado! Só pode usar UNFREEZE ou IMMUNITY.",
            type: "me"
        };
        return;
    }
    GameService.playGlobalPower(activePower.id, activePower.type);
    component.clearSelection();
}

export function handleDiscardBtn(component, activePower) {
    GameService.discardPower(activePower.id);
    component.clearSelection();
}