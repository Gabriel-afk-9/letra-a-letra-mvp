class ReactiveStore {
    constructor(initialState) {
        this.listeners = {};
        this.state = new Proxy(initialState, {
            set: (target, key, value) => {
                target[key] = value;
                this.notify(key, value);
                return true;
            }
        });
    }

    subscribe(key, callback) {
        if (!this.listeners[key]) this.listeners[key] = [];
        this.listeners[key].push(callback);
    }

    notify(key, value) {
        if (this.listeners[key]) {
            this.listeners[key].forEach(callback => callback(value));
        }
    }
}

export const store = new ReactiveStore({
    currentPage: 'home',
    user: { id: null, token: null, name: null },
    opponent: { id: null, name: '???' },
    tokenGameId: null,
    currentTurnPlayerId: null,
    board: [],
    words: [],
    players: [],
    endGameState: { 
        show: false, 
        title: '', 
        message: '', 
        type: ''
    },
    activePower: null,
    playerEffects: {
        blind: false,
        spy: false,
        freeze: false,
        immunity: false,
        detect_traps: false,
    }
});