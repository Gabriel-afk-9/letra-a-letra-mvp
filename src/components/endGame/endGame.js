import { store } from "../../state/store.js";
import { createEndGameTemplate } from "./endGame.template.js";

export class EndGameComponent extends HTMLElement {
    
    connectedCallback() {
        this.innerHTML = createEndGameTemplate();
        
        this.cacheElements();
        this.setupListeners();
        this.setupSubscriptions();
    }

    cacheElements() {
        this.overlay = this.querySelector('#end-overlay');
        this.titleEl = this.querySelector('#end-title');
        this.messageEl = this.querySelector('#end-message');
        this.btnHome = this.querySelector('#btn-home');
    }

    setupListeners() {
        this.btnHome.addEventListener('click', () => {
            store.state.endGameState = { show: false };
            store.state.tokenGameId = null;
            store.state.currentPage = 'home';
        });
    }

    setupSubscriptions() {
        store.subscribe('endGameState', (state) => this.render(state));
    }

    getResultClass(isWinner) {
        return isWinner ? 'winner' : 'loser';
    }

    render(state) {
        if (!state || !state.show) {
            this.overlay.className = 'end-overlay hidden';
            return;
        }

        const resultClass = this.getResultClass(state.isWinner);
        this.overlay.className = `end-overlay show ${resultClass}`;

        this.titleEl.innerText = state.title;
        this.messageEl.innerText = state.message;
    }
}

customElements.define("end-game-component", EndGameComponent);