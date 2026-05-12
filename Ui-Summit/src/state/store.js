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
        if (!this.listeners[key]) {
            this.listeners[key] = [];
        }
        this.listeners[key].push(callback);
    }

    notify(key, value) {
        if (this.listeners[key]) {
            this.listeners[key].forEach(callback => callback(value));
        }
    }
}

export const store = new ReactiveStore({
    currentPage: 'home', // 'home', 'matchmaking', 'game'
    user: { id: null, token: null, name: null },
    opponentName: '???',
    tokenGameId: null,
    currentTurnPlayerId: null,
    board:[]
});