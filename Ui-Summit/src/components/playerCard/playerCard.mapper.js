import { Selectors } from "../../state/selectors.js";

export function mapPlayerCardData(playerId, state) {
    const isMe = Selectors.isMe(playerId);
    const playerData = state.players?.find(p => p.id === playerId);

    return {
        name: isMe ? Selectors.getCurrentPlayer()?.name : Selectors.getOpponent()?.name,
        theme: isMe ? "theme-orange" : "theme-blue",
        powerCount: playerData?.inventory?.filter(Boolean).length || 0,
        avatar: isMe ? Selectors.getCurrentPlayer()?.avatar : Selectors.getOpponent()?.avatar
    };
}