import { store } from "../state/store.js";
import { wsManager } from "../websocket/socket.js";

export const GameService = {
    playTurn(x, y, powerId = null, powerName = null) {
        if (store.state.currentTurnPlayerId !== store.state.user.id) {
            console.warn("Não é o seu turno!");
            return;
        }

        const actionPayload = powerId ? {
            type: powerName,
            actionId: powerId,
            position: { x, y }
        } : {
            type: "REVEAL",
            position: { x, y }
        };

        wsManager.send({
            type: "PLAYER_ACTION",
            tokenGameId: store.state.tokenGameId,
            action: actionPayload
        });
    },

    discardPower(powerId) {
        wsManager.send({
            type: "DISCARD_POWER",
            tokenGameId: store.state.tokenGameId,
            powerId: powerId
        });
    },

    // leaveGame(){
    //     if (!store.state.tokenGameId) return;

    //     wsManager.send({
    //         type: "LEFT_GAME",
    //         tokenGameId: store.state.tokenGameId
    //     });
        
    // }
};