import { store } from "../../state/store.js";
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
            const getHash = (str) => {
                let sum = 0;
                for (let i = 0; i < str.length; i++) sum += str.charCodeAt(i);
                return sum;
            };

            let myAvatarId = (getHash(myData.id) % 4) + 1;
            let oppAvatarId = (getHash(opponentData.id) % 4) + 1;

            if (myAvatarId === oppAvatarId) {
                if (myData.id > opponentData.id) myAvatarId = (myAvatarId % 4) + 1;
                else oppAvatarId = (oppAvatarId % 4) + 1;
            }

            store.state.user = { ...store.state.user, avatar: `assets/avatar/avatar-${myAvatarId}.png` };
            oppAvatar = `assets/avatar/avatar-${oppAvatarId}.png`;
        }

        store.state.tokenGameId = msg.tokenGameId;
        store.state.opponent = opponentData
            ? { id: opponentData.id, name: opponentData.nickname, avatar: oppAvatar }
            : { id: null, name: "Desafiante", avatar: oppAvatar };
        callbacks.onMatchFound();
    },

    "PLAYER_ACTION_RESULT": (msg, userId, callbacks) => {
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
        }

        store.state.apiError = msg.message;
        if (store.notify) store.notify('apiError');
        store.state.activePower = null;
        UiModeService.clear();

        if (msg.message === "player_not_in_game") {
            GameActions.clearGameState();
            GameActions.setEndGameState(
                true, true, "🏃 OPONENTE FUGIU",
                "Você venceu! O oponente foi desconectado."
            );
        }
    }
};

function handleDisconnect() {
    if (!store.state.tokenGameId) return;
    GameActions.clearGameState();
    GameActions.setEndGameState(
        true, true, "🏃 OPONENTE FUGIU",
        "Você venceu por W.O.! O oponente saiu da partida."
    );
}

export const wsEventHandler = {
    handle(msg, userId, callbacks) {
        const newTurnId     = msg.currentTurnPlayerId    || msg.data?.currentTurnPlayerId;
        const newTurnEndsAt = msg.turnEndsAt             || msg.data?.turnEndsAt;

        if (newTurnId)     store.state.currentTurnPlayerId = newTurnId;

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
