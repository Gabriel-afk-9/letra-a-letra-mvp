import { store } from "../../state/store.js";

export const wsEventHandler = {
    handle(msg, userId, callbacks) {
        if (msg.data?.board) this.syncGameState(msg.data);

        switch (msg.event) {
            case "MATCHMAKING_GAME":
                if (msg.status === "FOUNDED") {
                    const opponent = msg.data.players.find(p => p.id !== userId);
                    store.state.tokenGameId = msg.tokenGameId;
                    store.state.opponent = opponent
                        ? { id: opponent.id, name: opponent.nickname }
                        : { id: null, name: "Desafiante" };
                    this.syncGameState(msg.data);
                    store.state.currentPage = 'found';
                    callbacks.onMatchFound();
                }
                break;

            case "PLAYER_ACTION_RESULT":
            case "TURN_EXPIRED":
                if (msg.data?.currentTurnPlayerId) {
                    store.state.currentTurnPlayerId = msg.data.currentTurnPlayerId;
                    this.updateTurnMessage();
                }
                callbacks.onResetWatchdog();
                break;

            case "GAME_OVER":
                const isWinner = msg.data.winner.id === store.state.user.id;
                callbacks.onGameEnd(isWinner ? "🏆 VOCÊ VENCEU!" : "💀 VOCÊ PERDEU!");
                break;

            case "REMOVED_BECAUSE_INACTIVITY":
                callbacks.onGameEnd("Você foi desconectado por inatividade.");
                break;

            case "PARTICIPANT_LEAVE":
            case "PARTICIPANT_DISCONNECTED":
                if (store.state.tokenGameId) callbacks.onGameEnd("Você venceu! O oponente abandonou.");
                break;

            case "ERROR":
                if (msg.message === "player_not_in_game")
                    callbacks.onGameEnd("🏆 Você venceu! O oponente foi desconectado.");
                break;
        }
    },

    syncGameState(data) {
        if (data.board) store.state.board = data.board;
        if (data.words) store.state.words = data.words;
        if (data.players) store.state.players = data.players;
        if (data.currentTurnPlayerId) {
            store.state.currentTurnPlayerId = data.currentTurnPlayerId;
            this.updateTurnMessage();
        }
    },

    updateTurnMessage() {
        const isMyTurn = store.state.currentTurnPlayerId === store.state.user.id;
        store.state.gameMessage = isMyTurn ? "🟢 SEU TURNO" : "🔴 TURNO DO OPONENTE";
    }
};