import { store } from "../../state/store.js";
import { GameActions } from "../../state/gameActions.js";
import { EFFECT_EVENTS, EFFECT_MAP } from "../../constants/effects.js";
import { Selectors } from "../../state/selectors.js";

const isTargetMe = (data) => {
    if (!data) return false;
    return Object.values(data).includes(Selectors.getCurrentPlayer()?.id);
};

const handleWordFound = ({ cells, foundedBy }) => {
    if (!cells || !foundedBy) return;

    const currentMap = store.state.foundCellsMap || {};
    cells.forEach(({ x, y }) => {
        currentMap[`${x},${y}`] = foundedBy;
    });
    store.state.foundCellsMap = { ...currentMap };

    setTimeout(() => {
        store.state.notification = Selectors.isMe(foundedBy)
            ? { message: "VocÃª encontrou uma palavra!", type: "me" }
            : { message: "O oponente encontrou uma palavra!", type: "opponent" };
    }, 100);
};

export const processInternalEvents = (events) => {
    events.forEach(({ event: eventName, data }) => {
        console.log(`[EVENTO DA RODADA] Nome: "${eventName}" | Dados:`, data);

        if (eventName === "TRAP_TRIGGERED" || eventName === "TRAP_REMOVED" || eventName === "TRAP_DETECTED") {
            if (data?.cell?.x !== undefined && data?.cell?.y !== undefined) {
                GameActions.requestCellAnimation("trap", data.cell.x, data.cell.y);
            }
            return;
        }

        if (eventName === "WORD_FOUNDED" || eventName === "WORD_FOUND") {
            handleWordFound(data);
            return;
        }

        if (eventName === "TURN_PASSED" && isTargetMe(data)) {
            if (Selectors.isFrozen()) {
                store.state.freezeTurnsLeft = (store.state.freezeTurnsLeft || 3) - 1;
                
                if (store.state.freezeTurnsLeft <= 0) {
                    store.state.pendingUnfreeze = true;
                }
            }
            return; 
        }

        if (eventName === "CELL_REVEALED" && Selectors.isMe(data?.revealedBy)) {
            if (Selectors.isBlind()) {
                store.state.blindTurnsLeft = (store.state.blindTurnsLeft || 3) - 1;
                
                if (store.state.blindTurnsLeft <= 0) {
                    store.state.pendingUnblind = true; 
                }            
            }
        }

        if (EFFECT_EVENTS.has(eventName) && isTargetMe(data)) {
            if (eventName === "PLAYER_USE_IMMUNITY") {
                GameActions.setEffect('freeze', false);
                GameActions.setEffect('blind', false);
                GameActions.setEffect('immunity', true);
                
                store.state.pendingUnfreeze = false;
                store.state.pendingUnblind = false;
                return;
            }

            const effect = EFFECT_MAP[eventName];
            if (effect) {
                GameActions.setEffect(effect.key, effect.value);
            }

            if (eventName === "PLAYER_FROZEN") {
                store.state.freezeTurnsLeft = 3;
                store.state.pendingUnfreeze = false;
            }

            if (eventName === "PLAYER_BLINDED") {
                store.state.blindTurnsLeft = 3;
                store.state.pendingUnblind = false;
            }
        }
    });
};
