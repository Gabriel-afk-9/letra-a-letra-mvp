import { store, getDistinctAvatars } from "./store.js";
import { UiModeService } from "../services/ui/uiModeService.js";
import { Selectors } from "./selectors.js";

let cellAnimationId = 0;

export const GameActions = {
    clearGameState() {
        const newAvatars = getDistinctAvatars();
        store.state.tokenGameId = null;
        store.state.currentTurnPlayerId = null;
        store.state.board = [];
        store.state.words = [];
        store.state.players = [];
        store.state.foundCellsMap = {};
        store.state.turnEndsAt = null;
        store.state.cellAnimation = null;
        store.state.activePower = null;
        store.state.notification = null;
        store.state.apiError = null;
        store.state.gameMessage = '';
        store.state.user = { ...store.state.user, avatar: newAvatars.p1 };
        store.state.opponent = { id: null, name: '???', avatar: newAvatars.p2 };
        store.state.opponentName = null;
        store.state.pendingUnfreeze = false;
        store.state.pendingUnblind = false;
        store.state.freezeTurnsLeft = 0;
        store.state.blindTurnsLeft = 0;
        store.state.playerEffects = {
            blind: false, spy: false, freeze: false,
            immunity: false, detect_traps: false
        };
        UiModeService.clearGameClasses();
    },

    setEffect(key, value) {
        store.state.playerEffects = { ...store.state.playerEffects, [key]: value };

        if (key === 'freeze') {
            if (value) {
                document.body.classList.add('is-frozen');
            } else {
                document.body.classList.remove('is-frozen');
                store.state.activePower = null;
                UiModeService.clear();
            }
        }
    },

    syncGameState(data) {
        if (data.board)   store.state.board   = data.board;
        if (data.words)   store.state.words   = data.words;
        if (data.players) store.state.players = data.players;

        if (data.currentTurnPlayerId) {
        store.state.currentTurnPlayerId = data.currentTurnPlayerId;
        this.updateTurnMessage();
    }
    },

    updateTurnMessage() {
        const isMyTurn = Selectors.isMyTurn();
        store.state.gameMessage = isMyTurn ? "🟢 SEU TURNO" : "🔴 TURNO DO OPONENTE";
    
        if (isMyTurn) {
            if (store.state.pendingUnfreeze) {
                this.setEffect('freeze', false);
                store.state.pendingUnfreeze = false;
            }

            if (store.state.pendingUnblind) {
                    this.setEffect('blind', false);
                    store.state.pendingUnblind = false;
            }
        }
    },  

    setEndGameState(show, isWinner, title, message) {
        store.state.endGameState = { show, isWinner, title, message };
    },

    requestCellAnimation(type, x, y) {
        store.state.cellAnimation = {
            id: ++cellAnimationId,
            type,
            x,
            y
        };
    }
};
