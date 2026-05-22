export function mapPlayerCardData(playerId, state) {
    const isMe = playerId === state.user?.id;
    const playerData = state.players?.find(p => p.id === playerId);

    return {
        name: isMe ? state.user?.name : state.opponent?.name,
        theme: isMe ? "theme-orange" : "theme-blue",
        powerCount: playerData?.inventory?.filter(Boolean).length || 0
    };
}