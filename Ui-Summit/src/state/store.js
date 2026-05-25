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

        return () => {
            this.listeners[key] = this.listeners[key].filter(listener => listener !== callback);
        };
    }

    notify(key, value) {
        if (this.listeners[key]) {
            this.listeners[key].forEach(callback => callback(value));
        }
    }
}

export function getDistinctAvatars() {
    const ids = [1, 2, 3, 4];
    for (let i = ids.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [ids[i], ids[j]] = [ids[j], ids[i]];
    }
    return {
        p1: `assets/avatar/avatar-${ids[0]}.png`,
        p2: `assets/avatar/avatar-${ids[1]}.png`
    };
}

const initialAvatars = getDistinctAvatars();

export const store = new ReactiveStore({
    currentPage: 'home',
    user: { id: null, token: null, name: null, avatar: initialAvatars.p1 },
    opponent: { id: null, name: '???', avatar: initialAvatars.p2 },
    opponentName: null,
    tokenGameId: null,
    currentTurnPlayerId: null,
    board: [],
    words: [],
    players: [],
    endGameState: { 
        show: false, 
        isWinner: false,
        title: '', 
        message: '', 
        type: ''
    },
    notification: null,
    apiError: null,
    gameMessage: '',
    foundCellsMap: {},
    turnEndsAt: null,
    cellAnimation: null,
    activePower: null,
    pendingUnfreeze: false,
    pendingUnblind: false,
    freezeTurnsLeft: 0,
    blindTurnsLeft: 0,
    playerEffects: {
        blind: false,
        spy: false,
        freeze: false,
        immunity: false,
        detect_traps: false,
    }
});
