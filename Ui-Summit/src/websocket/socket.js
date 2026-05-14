import { store } from "../state/store.js";

const wsUrl = "ws://localhost:8080/ws/game?token=";
export let gameWs = null;

export const wsManager = {
    connect(token, userId) {
        gameWs = new WebSocket(`${wsUrl}${token}`);

        gameWs.onopen = () => {
            console.log("🟢 WebSocket Aberto.");
            this.send({ type: "MATCHMAKING_GAME", gameMode: "CATACLYSM" });
        };

        gameWs.onmessage = (event) => {
            const msg = JSON.parse(event.data);
            console.log("📩 Evento Recebido:", msg.event, msg);

            if (msg.event === "MATCHMAKING_GAME" && msg.status === "FOUNDED") {
                const opponentInfo = msg.data.players.find(p => p.id !== userId);
                
                store.state.tokenGameId = msg.tokenGameId;
                store.state.opponent = opponentInfo ? 
                    { id: opponentInfo.id, name: opponentInfo.nickname } : 
                    { id: null, name: "Desafiante" };
                
                this.syncGameState(msg.data);

                store.state.currentPage = 'found';
            }

            if (msg.data && msg.data.board) {
                this.syncGameState(msg.data);
            }

            if (msg.event === "TURN_EXPIRED") {
                store.state.currentTurnPlayerId = msg.data.currentTurnPlayerId;
                this.updateTurnMessage();
            }

            if (msg.event === "REMOVED_BECAUSE_INACTIVITY") {
                store.state.gameMessage = "REMOVIDO POR INATIVIDADE!";
            }

            if (msg.event === "GAME_OVER") {
                const isWinner = msg.data.winner.id === userId;
                store.state.gameMessage = isWinner ? "🏆 VOCÊ VENCEU!" : "💀 VOCÊ PERDEU!";
                store.state.tokenGameId = null;
            }
        };
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
    },

    send(payload) {
        if (gameWs && gameWs.readyState === WebSocket.OPEN) {
            gameWs.send(JSON.stringify(payload));
        }
    }
};