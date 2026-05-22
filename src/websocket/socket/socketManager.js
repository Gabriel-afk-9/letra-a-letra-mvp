import { store } from "../../state/store.js";
import { wsConnection } from "./wsConnection.js";
import { wsWatchdog } from "./wsWatchdog.js";
import { wsEventHandler } from "./wsEventHandler.js";

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
        if (!store.state.tokenGameId) return;
        const isMyTurn = store.state.currentTurnPlayerId === store.state.user.id;
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

    send: (payload) => wsConnection.send(payload)
};