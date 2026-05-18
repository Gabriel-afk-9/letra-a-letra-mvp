import { store } from "./store.js";

export const GameActions = {
    clearGameState() {
        store.state.activePower = null;
        store.state.pendingUnblind = false;
        store.state.playerEffects = {
            blind: false, spy: false, freeze: false,
            immunity: false, detect_traps: false
        };
        document.body.className = "";
    },

    setEffect(key, value) {
        store.state.playerEffects = { ...store.state.playerEffects, [key]: value };

        if (key === 'freeze') {
            if (value) {
                document.body.classList.add('is-frozen');
            } else {
                document.body.classList.remove('is-frozen');
                store.state.activePower = null;
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
        const isMyTurn = store.state.currentTurnPlayerId === store.state.user.id;
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
    }
};