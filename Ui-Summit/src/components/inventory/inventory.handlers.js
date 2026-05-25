import { store } from "../../state/store.js";
import { GameService } from "../../services/game/gameService.js";
import { PowerRulesService } from "../../services/game/powerRulesService.js";
import { UiModeService } from "../../services/ui/uiModeService.js";

const getCardElement = (component, id) => component.querySelector(`.power-card[data-id="${id}"]`);

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

    const cardEl = getCardElement(component, powerId);


    if (diffY > 50) {
        if (scope === "GLOBAL") {
            if (cardEl) cardEl.classList.add('anim-use')
            component.clearSelection()
            
            setTimeout(() => {
                GameService.playGlobalPower(powerId, powerType);
            }, 300)
        } else {
            store.state.notification = { message: "Selecione uma célula no tabuleiro!", type: "me" };
        }
    }     else if (diffY < -50) {
        if (cardEl) cardEl.classList.add('anim-discard');
        component.clearSelection();
        
        setTimeout(() => {
            GameService.discardPower(powerId);
        }, 300);
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

    const cardEl = getCardElement(component, activePower.id);
    if (cardEl) cardEl.classList.add('anim-use');
    
    component.clearSelection();

    setTimeout(() => {
        GameService.playGlobalPower(activePower.id, activePower.type);
    }, 300);
}

export function handleDiscardBtn(component, activePower) {
    const cardEl = getCardElement(component, activePower.id);
    if (cardEl) cardEl.classList.add('anim-discard');
    
    component.clearSelection();

    setTimeout(() => {
        GameService.discardPower(activePower.id);
    }, 300);
}