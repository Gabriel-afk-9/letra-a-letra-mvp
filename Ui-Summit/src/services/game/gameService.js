import { store } from "../../state/store.js";
import { wsManager } from "../../websocket/socket/socketManager.js";

const getTokenGameId = () => store.state.tokenGameId;

const buildActionPayload = (x, y, powerId, powerName) =>
    powerId
        ? { type: powerName, actionId: powerId, position: { x, y } }
        : { type: "REVEAL", position: { x, y } };

export const GameService = {
    canAct(actionName = "REVEAL") {
        if (store.state.currentTurnPlayerId !== store.state.user.id) return false;

        if (store.state.playerEffects?.freeze && actionName !== "UNFREEZE") {
            store.state.notification = { 
                message: "Ação bloqueada! Você está congelado 🧊.", 
                type: "me" 
            };
            return false;
        }
        return true;
    },
    playTurn(x, y, powerId = null, powerName = null) {
        if (store.state.currentTurnPlayerId !== store.state.user.id) return;

        wsManager.send({
            type: "PLAYER_ACTION",
            tokenGameId: getTokenGameId(),
            action: buildActionPayload(x, y, powerId, powerName)
        });
    },

    playGlobalPower(powerId, powerName) {
        if (!this.canAct(powerName)) return;

        const offensivePowers = ["FREEZE", "BLIND"];
    
        const target = offensivePowers.includes(powerName) 
            ? store.state.opponent.id 
            : store.state.user.id;

        wsManager.send({
            type: "PLAYER_ACTION",
            tokenGameId: getTokenGameId(),
            action: { type: powerName, actionId: powerId, targetId: target }
        });
    },

    discardPower(powerId) {
        wsManager.send({
            type: "DISCARD_POWER",
            tokenGameId: getTokenGameId(),
            powerId
        });
    },

    leaveGame(isClosingTab = false) {
        if (!getTokenGameId()) return;

        wsManager.send({
            type: "LEFT_GAME",
            tokenGameId: getTokenGameId()
        });

        store.state.tokenGameId = null;
        store.state.playerEffects = {
            blind: false, spy: false, freeze: false, immunity: false, detect_traps: false
        };
        document.body.className = "";

        if (!isClosingTab) store.state.currentPage = 'home';
    }
};