import { AppConfig } from "../../config/app.config.js";

export const wsConnection = {
    socket: null,

    connect(token, onMessage) {
        this.disconnect();
        this.socket = new WebSocket(`${AppConfig.WS_URL}?token=${token}`);
        this.socket.onopen = () => this.send({ type: "MATCHMAKING_GAME", gameMode: "CATACLYSM" });
        this.socket.onmessage = (event) => onMessage(JSON.parse(event.data));
    },

    send(payload) {
        if (this.socket?.readyState === WebSocket.OPEN) {
            this.socket.send(JSON.stringify(payload));
        }
    },

    disconnect() {
        if (this.socket) {
            this.socket.onopen = null;
            this.socket.onmessage = null;
            this.socket.close();
            this.socket = null;
        }
    }
};