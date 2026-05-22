import { store } from "../../state/store.js";
import { GameService } from "../../services/game/gameService.js";

export function handleCellClick(component, cell) {
    const { user, playerEffects, currentTurnPlayerId, activePower } = store.state;
    
    const isFreeze = playerEffects?.freeze;
    const isNotMyTurn = currentTurnPlayerId !== user?.id;

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
        document.body.className = "";
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