import { store } from "../state/store.js";

export class EndGameModal extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
            <div class="modal-overlay" id="end-modal" style="display: none;">
                <div class="modal-box" id="modal-content">
                    <h2 id="modal-title"></h2>
                    <p id="modal-message"></p>
                    <button class="button" id="btn-home">Voltar ao Início</button>
                </div>
            </div>
        `;

        this.overlayEl = this.querySelector('#end-modal');
        this.contentEl = this.querySelector('#modal-content');
        this.titleEl = this.querySelector('#modal-title'); 
        this.messageEl = this.querySelector('#modal-message'); 
        
        this.querySelector('#btn-home').addEventListener('click', () => {
            store.state.endGameState = { show: false, title: '', message: '', type: '' };
            store.state.currentPage = 'home';
        });

        store.subscribe('endGameState', (state) => this.render(state));
    }

    render(state) {
        if (!state || !state.show) {
            this.overlayEl.style.display = 'none';
            return;
        }

        this.titleEl.innerText = state.title;
        this.messageEl.innerText = state.message;
        
        this.contentEl.className = `modal-box ${state.type}`;
        this.overlayEl.style.display = 'flex';
    }
}
customElements.define("end-modal", EndGameModal);