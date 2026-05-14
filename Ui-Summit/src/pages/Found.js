import { store } from "../state/store.js";

export class FoundPage extends HTMLElement {
    connectedCallback() {
        const myName = store.state.user.name || "Você";
        const oppName = store.state.opponent.name || "Oponente";

        this.innerHTML = `
            <div class="found-container">
                <div class="card">
                    <h2 class="found-title">Oponente Encontrado!</h2>
                    
                    <div class="found-vs-box">
                        <div class="found-player">
                            <img class="found-avatar found-avatar--local" src="public/avatar-placeholder.png" alt="Seu Avatar">
                            <p class="found-name">${myName}</p>
                        </div>
                        
                        <span class="found-vs-badge">VS</span>
                        
                        <div class="found-player">
                            <img class="found-avatar found-avatar--opponent" src="public/avatar-placeholder.png" alt="Avatar do Oponente">
                            <p class="found-name">${oppName}</p>
                        </div>
                    </div>
                </div>

                <p id="connection-status" class="found-status">Preparando o tabuleiro...</p>
            </div>
        `;

        setTimeout(() => {
            const status = this.querySelector("#connection-status");
            if (status) status.innerText = "Pronto! Começando...";
            
            setTimeout(() => {
                store.state.currentPage = 'game';
            }, 800);
        }, 2500);
    }
}
customElements.define("found-page", FoundPage);