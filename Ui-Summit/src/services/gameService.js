import { store } from "../state/store.js";
import { wsManager } from "../websocket/socket.js";

const API_URL = "http://localhost:8080";

export const GameService = {
    async registerAndLogin(nickname) {
        const email = `${nickname}@email.com`;
        const password = "12345678";

        await fetch(`${API_URL}/user`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ nickname, email, password })
        });

        const res = await fetch(`${API_URL}/user/login`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email, password })
        }).then(r => r.json());

        if (res.data) {
            store.state.user = { id: res.data.id, nickname, token: res.data.token };
            return true;
        }
        return false;
    },

    playTurn(x, y) {
        if (store.state.currentTurnPlayerId !== store.state.user.id) return;

        wsManager.send({
            type: "PLAYER_ACTION",
            tokenGameId: store.state.tokenGameId,
            action: { type: "REVEAL", position: { x, y } }
        });
    }
};