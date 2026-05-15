import { store } from "../../state/store.js";
import { wsManager } from "../../websocket/socket/socketManager.js";

const getTokenGameId = () => store.state.tokenGameId;

const buildActionPayload = (x, y, powerId, powerName) =>
    powerId
        ? { type: powerName, actionId: powerId, position: { x, y } }
        : { type: "REVEAL", position: { x, y } };

export const GameService = {
    playTurn(x, y, powerId = null, powerName = null) {
        if (store.state.currentTurnPlayerId !== store.state.user.id) {
            console.warn("Não é o seu turno!");
            return;
        }
        
        wsManager.send({
            type: "PLAYER_ACTION",
            tokenGameId: getTokenGameId(),
            action: buildActionPayload(x, y, powerId, powerName)
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
        
        if (!isClosingTab) {
            store.state.currentPage = 'home';
        }
    }
};