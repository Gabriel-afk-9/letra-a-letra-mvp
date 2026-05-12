// import { store } from "../state/store.js";

// class WebSocketManager {
//     constructor() {
//         this.ws = null;
//         this.wsUrl = "ws://localhost:8080/ws/game?token=";
//     }

//     connect(token, userId) {
//         this.ws = new WebSocket(`${this.wsUrl}${token}`);

//         this.ws.onopen = () => {
//             console.log("🟢 Conectado ao Jogo");
//             this.send({ type: "MATCHMAKING_GAME", gameMode: "NORMAL" });
//             store.state.gameStatus = 'MATCHMAKING';
//         };

//         this.ws.onmessage = (event) => {
//             const msg = JSON.parse(event.data);
//             this.handleMessage(msg, userId);
//         };

//         this.ws.onclose = () => console.log("🔴 Desconectado");
//     }

//     handleMessage(msg, myUserId) {
//         if (msg.event === "MATCHMAKING_GAME" && msg.status === "FOUNDED") {
//             store.state.tokenGameId = msg.tokenGameId;
//             store.state.currentTurnPlayerId = msg.data.currentTurnPlayerId;
            
//             const opponent = msg.data.players.find(p => p.id !== myUserId);
//             if (opponent) store.state.opponent = opponent;
            
//             store.state.gameStatus = 'PLAYING';
//         }

//         if (msg.data && msg.data.board) {
//             store.state.board = msg.data.board;
//         }

//         if (msg.event === "GAME_OVER") {
//             store.state.gameStatus = 'GAME_OVER';
//         }
//     }

//     send(payload) {
//         if (this.ws && this.ws.readyState === WebSocket.OPEN) {
//             this.ws.send(JSON.stringify(payload));
//         }
//     }
// }

// export const wsManager = new WebSocketManager();

import { store } from "../state/store.js";

const wsUrl = "ws://localhost:8080/ws/game?token=";
export let gameWs = null;

export const wsManager = {
    connect(token, userId) {
        gameWs = new WebSocket(`${wsUrl}${token}`);

        gameWs.onopen = () => {
            console.log("🟢 WebSocket Aberto. Pedindo partida...");
            gameWs.send(JSON.stringify({
                type: "MATCHMAKING_GAME",
                gameMode: "NORMAL"
            }));
        };

        gameWs.onmessage = (event) => {
            const msg = JSON.parse(event.data);
            console.log("📩 WS Recebido:", msg);

            // 1. Quando o jogo é encontrado no Matchmaking
            if (msg.event === "MATCHMAKING_GAME" && msg.status === "FOUNDED") {
                let opponentName = "Desafiante";

                if (msg.data && msg.data.players) {
                    const opponent = msg.data.players.find(p => p.id != userId);
                    if (opponent) opponentName = opponent.nickname;
                }

                // Salva as credenciais da partida
                store.state.tokenGameId = msg.tokenGameId;
                store.state.currentTurnPlayerId = msg.data.currentTurnPlayerId;
                store.state.opponentName = opponentName;

                // 🚨 AQUI ESTÁ A CORREÇÃO: Salva o tabuleiro inicial na Store!
                if (msg.data && msg.data.board) {
                    store.state.board = msg.data.board;
                }

                // Muda de página após 4 segundos
                setTimeout(() => {
                    store.state.currentPage = 'game';
                }, 4000);
            }

            // 2. Quando houver uma jogada ou o turno expirar (atualiza o board)
            // Se qualquer mensagem do WS trouxer um board novo, atualizamos a Store!
            if (msg.data && msg.data.board) {
                store.state.board = msg.data.board;
            }
            
            // Atualiza de quem é o turno caso o servidor mande
            if (msg.data && msg.data.currentTurnPlayerId) {
                store.state.currentTurnPlayerId = msg.data.currentTurnPlayerId;
            }
        };

        gameWs.onerror = (err) => {
            console.error("🔴 Erro no WebSocket:", err);
            store.state.currentPage = 'home';
        };
    }
};