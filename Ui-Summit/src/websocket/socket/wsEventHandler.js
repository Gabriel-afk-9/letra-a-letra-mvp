import { store } from "../../state/store.js";
import { GameActions } from "../../state/gameActions.js";
import { processInternalEvents } from "../handlers/internalEvents.js";

export const wsEventHandler = {
    handle(msg, userId, callbacks) {
        if (Array.isArray(msg.events)) {
            processInternalEvents(msg.events);
        }

        if (msg.data?.board) {
            GameActions.syncGameState(msg.data);
        }

        this.routeEvent(msg, userId, callbacks);
    },

    routeEvent(msg, userId, callbacks) {
        switch (msg.event) {
            case "MATCHMAKING_GAME":
                if (msg.status !== "FOUNDED") break;
                const opponent = msg.data.players.find(p => p.id !== userId);
                store.state.tokenGameId = msg.tokenGameId;
                store.state.opponent = opponent
                    ? { id: opponent.id, name: opponent.nickname }
                    : { id: null, name: "Desafiante" };
                store.state.currentPage = 'found';
                callbacks.onMatchFound();
                break;

            case "PLAYER_ACTION_RESULT":
            case "TURN_EXPIRED":
                if (msg.data?.currentTurnPlayerId) {
                    store.state.currentTurnPlayerId = msg.data.currentTurnPlayerId;
                    store.state.turnEndsAt = msg.turnEndsAt;
                    GameActions.updateTurnMessage();
                }
                callbacks.onResetWatchdog();
                break;

            case "GAME_OVER": {
                GameActions.clearGameState();
                const isWinner = msg.data.winner.id === store.state.user.id;
                GameActions.setEndGameState(
                    true, isWinner,
                    isWinner ? "🏆 VOCÊ VENCEU!" : "💀 VOCÊ PERDEU!",
                    isWinner ? "Parabéns! Você encontrou mais palavras."
                             : "O oponente foi melhor dessa vez. Tente novamente!"
                );
                break;
            }

            case "REMOVED_BECAUSE_INACTIVITY":
                GameActions.clearGameState();
                GameActions.setEndGameState(
                    true, false, "💤 DESCONECTADO",
                    "Você foi removido por inatividade."
                );
                break;

            case "PARTICIPANT_LEAVE":
            case "PARTICIPANT_DISCONNECTED":
                if (!store.state.tokenGameId) break;
                GameActions.clearGameState();
                GameActions.setEndGameState(
                    true, true, "🏃 OPONENTE FUGIU",
                    "Você venceu por W.O.! O oponente saiu da partida."
                );
                break;

            case "ERROR":
                store.state.activePower = null;
                document.body.className = "";
                if (msg.message === "player_not_in_game") {
                    GameActions.clearGameState();
                    GameActions.setEndGameState(
                        true, true, "🏃 OPONENTE FUGIU",
                        "Você venceu! O oponente foi desconectado."
                    );
                }
                break;
        }
    }
};