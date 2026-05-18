import { store } from "../state/store.js";

export class EndGameComponent extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
            <div id="end-overlay" class="end-overlay hidden">
                <div class="end-modal" id="end-modal">
                    <h1 id="end-title"></h1>
                    <p id="end-message"></p>
                    <button id="btn-home">Voltar ao Início</button>
                </div>
            </div>
        `;

        this.overlay = this.querySelector('#end-overlay');
        this.titleEl = this.querySelector('#end-title');
        this.messageEl = this.querySelector('#end-message');
        this.btnHome = this.querySelector('#btn-home');

        this.btnHome.addEventListener('click', () => {
            store.state.endGameState = { show: false };
            store.state.tokenGameId = null;
            store.state.currentPage = 'home';
        });

        store.subscribe('endGameState', (state) => this.render(state));
    }

    render(state) {
        if (!state || !state.show) {
            this.overlay.className = 'end-overlay hidden';
            return;
        }

        const resultClass = state.isWinner ? 'winner' : 'loser';
        this.overlay.className = `end-overlay show ${resultClass}`;

        this.titleEl.innerText = state.title;
        this.messageEl.innerText = state.message;
    }
}
customElements.define("end-game-component", EndGameComponent);