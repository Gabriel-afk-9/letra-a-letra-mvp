import { store } from "./store.js";

export const Selectors = {
    getCurrentPlayer: () => store.state.user,
    getOpponent: () => store.state.opponent,
    isMyTurn: () => store.state.currentTurnPlayerId === store.state.user?.id,
    isMe: (playerId) => playerId === store.state.user?.id,
    isFrozen: () => !!store.state.playerEffects?.freeze,
    isBlind: () => !!store.state.playerEffects?.blind,
    canDetectTraps: () => !!store.state.playerEffects?.detect_traps,
    hasActiveGame: () => !!store.state.tokenGameId,
    getActivePower: () => store.state.activePower,
    hasActivePower: () => !!store.state.activePower
};