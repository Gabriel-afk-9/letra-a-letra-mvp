import { store, getAvatarHash } from "../../state/store.js";
import { GameActions } from "../../state/gameActions.js";
import { processInternalEvents } from "../handlers/internalEvents.js";
import { UiModeService } from "../../services/ui/uiModeService.js";
import { Selectors } from "../../state/selectors.js";

let turnDurationMs = 30_000;

const eventStrategies = {
    "MATCHMAKING_GAME": (msg, userId, callbacks) => {
        if (msg.status !== "FOUNDED") return;
        
        const opponentData = msg.data.players.find(p => p.id !== userId);
        const myData = msg.data.players.find(p => p.id === userId);
        
        let oppAvatar = "assets/avatar/avatar-2.png";

        if (myData && opponentData) {
            oppAvatar = `assets/avatar/avatar-${getAvatarHash(opponentData.id)}.png`
        }

        store.state.tokenGameId = msg.tokenGameId;
        store.state.opponent = opponentData
            ? { id: opponentData.id, name: opponentData.nickname, avatar: oppAvatar }
            : { id: null, name: "Desafiante", avatar: oppAvatar };
        callbacks.onMatchFound();
    },

    "PLAYER_ACTION_RESULT": (msg, userId, callbacks) => {
        store.state.activePower = null;
        if (UiModeService && UiModeService.clear) UiModeService.clear();
        document.body.className = ""; 
        if (GameActions.updateTurnMessage) GameActions.updateTurnMessage();
        callbacks.onResetWatchdog();
    },

    "TURN_EXPIRED": (msg, userId, callbacks) => {
        store.state.turnEndsAt = new Date(Date.now() + turnDurationMs).toISOString();
        if (GameActions.updateTurnMessage) GameActions.updateTurnMessage();
        callbacks.onResetWatchdog();
    },

    "GAME_OVER": (msg) => {
        GameActions.clearGameState();
        const isWinner = Selectors.isMe(msg.data.winner.id);
        GameActions.setEndGameState(
            true, isWinner,
            isWinner ? "🏆 VOCÊ VENCEU!" : "💀 VOCÊ PERDEU!",
            isWinner ? "Parabéns! Você encontrou mais palavras."
                     : "O oponente foi melhor dessa vez. Tente novamente!"
        );
    },

    "REMOVED_BECAUSE_INACTIVITY": () => {
        GameActions.clearGameState();
        GameActions.setEndGameState(
            true, false, "💤 DESCONECTADO",
            "Você foi removido por inatividade."
        );
    },

    "PARTICIPANT_LEAVE": () => handleDisconnect(),
    "PARTICIPANT_DISCONNECTED": () => handleDisconnect(),

    "ERROR": (msg) => {
        if (msg.message === "stepped_on_trap" && msg.data?.x !== undefined) {
            GameActions.requestCellAnimation("trap", msg.data.x, msg.data.y);
            return;
        }
        if (msg.message === "player_are_immune" || msg.message.includes("imune")) {
            
            store.state.notification = { 
                message: "Ataque bloqueado! O oponente está imune 🛡️", 
                type: "opponent" 
            };
            
            if (UiModeService && UiModeService.clear) UiModeService.clear();
            document.body.className = "";
            
            return;
        }

        store.state.apiError = msg.message;
        if (store.notify) store.notify('apiError');

        store.state.activePower = null;
        if (UiModeService && UiModeService.clear) UiModeService.clear();
        document.body.className = "";

        if (msg.message === "player_not_in_game") {
            GameActions.clearGameState();
            GameActions.setEndGameState(
                true, true, "OPONENTE FUGIU",
                "Você venceu! O oponente foi desconectado."
            );
        }
    }
};

function handleDisconnect() {
    if (!store.state.tokenGameId) return;
    GameActions.clearGameState();
    GameActions.setEndGameState(
        true, true, "OPONENTE FUGIU",
        "Você venceu por W.O.! O oponente saiu da partida."
    );
}

export const wsEventHandler = {
    handle(msg, userId, callbacks) {
        const oldTurnId = store.state.currentTurnPlayerId;
        const newTurnId = msg.currentTurnPlayerId || msg.data?.currentTurnPlayerId;
        const newTurnEndsAt = msg.turnEndsAt || msg.data?.turnEndsAt;

        if (newTurnId) {
            store.state.currentTurnPlayerId = newTurnId;

            // 👇 DETECTOR DE MUDANÇA DE TURNO (À PROVA DE FALHAS) 👇
            // Se o turno MUDOU e agora é a SUA VEZ, diminui a contagem dos efeitos
            if (newTurnId !== oldTurnId && newTurnId === store.state.user.id) {
                
                if (store.state.playerEffects?.freeze) {
                    store.state.freezeTurnsLeft = (store.state.freezeTurnsLeft || 3) - 1;
                    if (store.state.freezeTurnsLeft <= 0) {
                        GameActions.setEffect('freeze', false); // Descongela na hora!
                    }
                }

                if (store.state.playerEffects?.immunity) {
                    store.state.immunityTurnsLeft = (store.state.immunityTurnsLeft || 5) - 1;
                    if (store.state.immunityTurnsLeft <= 0) {
                        GameActions.setEffect('immunity', false); // Estoura a bolha na hora!
                    }
                }
            }
            // 👆 ------------------------------------------------ 👆
        }

        if (newTurnEndsAt) {
            const remaining = new Date(newTurnEndsAt).getTime() - Date.now();
            if (remaining > 1000) turnDurationMs = remaining;
            store.state.turnEndsAt = newTurnEndsAt;
        }

        if (Array.isArray(msg.events)) {
            processInternalEvents(msg.events);
        }

        if (msg.data?.board) {
            GameActions.syncGameState(msg.data);
        }

        this.routeEvent(msg, userId, callbacks);
    },

    routeEvent(msg, userId, callbacks) {
        const strategy = eventStrategies[msg.event];
        if (strategy) {
            strategy(msg, userId, callbacks);
        } else {
            console.log(`[wsEventHandler] Evento não mapeado: ${msg.event}`);
        }
    }
};