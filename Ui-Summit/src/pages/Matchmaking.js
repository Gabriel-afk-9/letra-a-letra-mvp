import { store } from "../state/store.js";
import { wsManager } from "../websocket/socket/socketManager.js";

class MatchmakingPage extends HTMLElement {
    connectedCallback() {
        const myName = store.state.user.name || "Você";

        this.innerHTML = `
            <div class="matchmaking-container">
                <h1 id="mm-title">Buscando Oponente...</h1>
                <div id="mm-spinner" class="spinner"></div>
                
                <div id="mm-found" style="display: none;">
                    <h2 style="color: #4CAF50;">Oponente Encontrado!</h2>
                    <p class="vs-text">
                        <strong>${myName}</strong> <span style="color: red;">VS</span> <strong id="opponent-name">???</strong>
                    </p>
                    <p>Preparando a partida...</p>
                </div>
            </div>
        `;

        const { token, id } = store.state.user;
        if (token && id) {
            wsManager.connect(token, id);
        } else {
            console.error("Faltam dados do usuário para o WebSocket!");
            store.state.currentPage = 'home';
        }

        store.subscribe('opponentName', (newName) => {
            if (newName && newName !== '???') {
                this.querySelector("#mm-title").style.display = "none";
                this.querySelector("#mm-spinner").style.display = "none";
                
                const foundDiv = this.querySelector("#mm-found");
                foundDiv.style.display = "block";
                
                this.querySelector("#opponent-name").innerText = newName;
            }
        });
    }
}
customElements.define("matchmaking-page", MatchmakingPage);