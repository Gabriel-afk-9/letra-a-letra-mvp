// import { store } from "../../state/store.js";
// import { GameActions } from "../../state/gameActions.js";
// import { EFFECT_EVENTS, EFFECT_MAP } from "../../constants/effects.js";
// import { Selectors } from "../../state/selectors.js";

// const isTargetMe = (data) => {
//     if (!data) return false;
//     const myId = store.state.user.id;
//     return Object.values(data).includes(myId);
// };

// const handleWordFound = ({ cells, foundedBy }) => {
//     if (!cells || !foundedBy) return;

//     const currentMap = store.state.foundCellsMap || {};
//     cells.forEach(({ x, y }) => {
//         currentMap[`${x},${y}`] = foundedBy;
//     });
//     store.state.foundCellsMap = { ...currentMap };

//     setTimeout(() => {
//         store.state.notification = Selectors.isMe(foundedBy)
//             ? { message: "Você encontrou uma palavra!", type: "me" }
//             : { message: "O oponente encontrou uma palavra!", type: "opponent" };
//     }, 100);
// };

// export const processInternalEvents = (events) => {
//     events.forEach(({ event: eventName, data }) => {
        
//         if (eventName === "TRAP_TRIGGERED" || eventName === "TRAP_REMOVED" || eventName === "TRAP_DETECTED") {
//             if (data?.cell?.x !== undefined && data?.cell?.y !== undefined) {
//                 GameActions.requestCellAnimation("trap", data.cell.x, data.cell.y);
//             }
//             return;
//         }

//         if (eventName === "WORD_FOUNDED" || eventName === "WORD_FOUND") {
//             handleWordFound(data);
//             return;
//         }

//         if (eventName === "TURN_PASSED" && isTargetMe(data)) {
//             if (Selectors.isFrozen()) {
//                 store.state.freezeTurnsLeft = (store.state.freezeTurnsLeft || 3) - 1;
//                 if (store.state.freezeTurnsLeft <= 0) {
//                     store.state.pendingUnfreeze = true;
//                 }
//             }
//             if (store.state.playerEffects?.immunity) {
//                 store.state.immunityTurnsLeft = (store.state.immunityTurnsLeft || 3) - 1;
//                 if (store.state.immunityTurnsLeft <= 0) {
//                     store.state.pendingUnimmunity = true;
//                 }
//             }
//             return; 
//         }

//         if (eventName === "CELL_REVEALED" && Selectors.isMe(data?.revealedBy)) {
//             if (Selectors.isBlind()) {
//                 store.state.blindTurnsLeft = (store.state.blindTurnsLeft || 3) - 1;
//                 if (store.state.blindTurnsLeft <= 0) {
//                     store.state.pendingUnblind = true; 
//                 }            
//             }
//         }

//         if (EFFECT_EVENTS.has(eventName) && isTargetMe(data)) {
            
//             if (eventName === "PLAYER_USE_IMMUNITY") {
//                 console.log("🛡️ Você ativou a Imunidade!");
//                 GameActions.setEffect('freeze', false);
//                 GameActions.setEffect('blind', false);
//                 GameActions.setEffect('immunity', true);
                
//                 store.state.immunityTurnsLeft = 5;
                
//                 store.state.pendingUnfreeze = false;
//                 store.state.pendingUnblind = false;
//                 store.state.pendingUnimmunity = false;
//                 return;
//             }

//             const effect = EFFECT_MAP[eventName];
            
//             if (effect) {
//                 GameActions.setEffect(effect.key, effect.value);
//             }

//             if (eventName === "PLAYER_FROZEN") {
//                 store.state.freezeTurnsLeft = 3;
//                 store.state.pendingUnfreeze = false;
//             }

//             if (eventName === "PLAYER_BLINDED") {
//                 store.state.blindTurnsLeft = 3;
//                 store.state.pendingUnblind = false;
//             }
//         }
//     });
// };

import { store } from "../../state/store.js";
import { GameActions } from "../../state/gameActions.js";
import { EFFECT_EVENTS, EFFECT_MAP } from "../../constants/effects.js";
import { Selectors } from "../../state/selectors.js";

const isTargetMe = (data) => {
    if (!data) return false;
    const myId = store.state.user.id;
    return Object.values(data).includes(myId);
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
            ? { message: "Você encontrou uma palavra!", type: "me" }
            : { message: "O oponente encontrou uma palavra!", type: "opponent" };
    }, 100);
};

export const processInternalEvents = (events) => {
    events.forEach(({ event: eventName, data }) => {
        
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

        // 👇 BLOCO TURN_PASSED REMOVIDO! A contagem de turnos foi para o wsEventHandler.js 👇

        if (eventName === "CELL_REVEALED" && Selectors.isMe(data?.revealedBy)) {
            if (Selectors.isBlind()) {
                store.state.blindTurnsLeft = (store.state.blindTurnsLeft || 3) - 1;
                if (store.state.blindTurnsLeft <= 0) {
                    // Desliga a cegueira IMEDIATAMENTE (sai da tela na hora)
                    GameActions.setEffect('blind', false); 
                }            
            }
        }

        if (EFFECT_EVENTS.has(eventName) && isTargetMe(data)) {
            
            if (eventName === "PLAYER_USE_IMMUNITY") {
                console.log("🛡️ Você ativou a Imunidade!");
                GameActions.setEffect('freeze', false);
                GameActions.setEffect('blind', false);
                GameActions.setEffect('immunity', true);
                
                // Cravado em 5 turnos seus
                store.state.immunityTurnsLeft = 5;
                return;
            }

            const effect = EFFECT_MAP[eventName];
            
            if (effect) {
                GameActions.setEffect(effect.key, effect.value);
            }

            if (eventName === "PLAYER_FROZEN") {
                store.state.freezeTurnsLeft = 3;
            }

            if (eventName === "PLAYER_BLINDED") {
                store.state.blindTurnsLeft = 3;
            }
        }
    });
};