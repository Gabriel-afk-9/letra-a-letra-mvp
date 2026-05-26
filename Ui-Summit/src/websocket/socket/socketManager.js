import { store } from "../../state/store.js";
import { wsConnection } from "./wsConnection.js";
import { wsWatchdog } from "./wsWatchdog.js";
import { wsEventHandler } from "./wsEventHandler.js";
import { Selectors } from "../../state/selectors.js";
import { GameActions } from "../../state/gameActions.js";

export const wsManager = {
    connect(token, userId) {
        wsConnection.connect(token, (msg) => {
            wsEventHandler.handle(msg, userId, {
                onMatchFound: () => wsWatchdog.reset(() => this.handleWatchdogExpire()),
                onResetWatchdog: () => wsWatchdog.reset(() => this.handleWatchdogExpire()),
                onGameEnd: (isWinner, title, message) => this.endGame(isWinner, title, message),
            });
        });
    },

    handleWatchdogExpire() {
        if (!Selectors.hasActiveGame()) return;
        const isMyTurn = Selectors.isMyTurn();
        
        this.endGame(
            !isMyTurn, 
            isMyTurn ? "TEMPO ESGOTADO" : "VOCÊ VENCEU!",
            isMyTurn ? "Você perdeu por inatividade." : "O oponente foi desconectado por inatividade."
        );
    },

    endGame(isWinner, title, message) {
        wsWatchdog.stop();
        store.state.tokenGameId = null;
        GameActions.setEndGameState(true, isWinner, title, message);
    },

    send: (payload) => wsConnection.send(payload),

    disconnect() {
        wsWatchdog.stop();
        wsConnection.disconnect();
    }
};

document.addEventListener("visibilitychange", () => {
    if (document.visibilityState === "visible") {
        if (wsConnection.socket && wsConnection.socket.readyState !== WebSocket.OPEN) {
            
            store.state.tokenGameId = null;
            store.state.opponent = { id: null, name: '???' };
            
            GameActions.setEndGameState(
                true, false, "DESCONECTADO", 
                "Conexão perdida porque o jogo ficou em segundo plano."
            );
        }
    }
});

window.addEventListener("pagehide", () => {
    if (store.state.tokenGameId && wsConnection.socket?.readyState === WebSocket.OPEN) {
        wsConnection.send({
            type: "LEFT_GAME",
            tokenGameId: store.state.tokenGameId
        });
        wsConnection.socket.close();
    }
});