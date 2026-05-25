import { store } from "../../state/store.js";
import { wsConnection } from "./wsConnection.js";
import { wsWatchdog } from "./wsWatchdog.js";
import { wsEventHandler } from "./wsEventHandler.js";
import { Selectors } from "../../state/selectors.js";

export const wsManager = {
    connect(token, userId) {
        wsConnection.connect(token, (msg) => {
            wsEventHandler.handle(msg, userId, {
                onMatchFound: () => wsWatchdog.reset(() => this.handleWatchdogExpire()),
                onResetWatchdog: () => wsWatchdog.reset(() => this.handleWatchdogExpire()),
                onGameEnd: (message) => this.endGame(message),
            });
        });
    },

    handleWatchdogExpire() {
        if (!Selectors.hasActiveGame()) return;
        const isMyTurn = Selectors.isMyTurn();
        this.endGame(isMyTurn
            ? "Você perdeu por inatividade."
            : "🏆 Você venceu! O oponente foi desconectado."
        );
    },

    endGame(message) {
        wsWatchdog.stop();
        alert(message);
        store.state.tokenGameId = null;
        store.state.currentPage = 'home';
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
            
            alert("Conexão perdida porque o navegador foi minimizado. Voltando ao menu inicial.");
            store.state.currentPage = 'home';
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
