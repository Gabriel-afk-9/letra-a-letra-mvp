export const wsConnection = {
    socket: null,

    connect(token, onMessage) {
        this.socket = new WebSocket(`ws://localhost:8080/ws/game?token=${token}`);
        this.socket.onopen = () => this.send({ type: "MATCHMAKING_GAME", gameMode: "CATACLYSM" });
        this.socket.onmessage = (event) => onMessage(JSON.parse(event.data));
    },

    send(payload) {
        if (this.socket?.readyState === WebSocket.OPEN) {
            this.socket.send(JSON.stringify(payload));
        }
    }
};