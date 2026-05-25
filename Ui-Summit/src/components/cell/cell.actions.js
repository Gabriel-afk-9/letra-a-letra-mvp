import { store } from "../../state/store.js";
import { GameService } from "../../services/game/gameService.js";
import { UiModeService } from "../../services/ui/uiModeService.js";
import { Selectors } from "../../state/selectors.js";

export function handleCellClick(component, cell) {
    const activePower = Selectors.getActivePower();
    
    const isFreeze = Selectors.isFrozen();
    const isNotMyTurn = !Selectors.isMyTurn();

    if (isNotMyTurn || isFreeze) {
        component.classList.add('shake-error');
        setTimeout(() => component.classList.remove('shake-error'), 400);

        if (isFreeze) {
            store.state.notification = { message: "Você está congelado! 🧊", type: "me" };
        }
        return;
    }

    if (activePower?.scope === "CELL") {
        GameService.playTurn(cell.x, cell.y, activePower.id, activePower.type);
        store.state.activePower = null;
        UiModeService.clear();
        return;
    }

    if (activePower?.scope === "GLOBAL") {
        store.state.notification = {
            message: "Este poder não precisa de célula! Arraste para cima.",
            type: "me"
        };
        return;
    }

    GameService.playTurn(cell.x, cell.y);
}
