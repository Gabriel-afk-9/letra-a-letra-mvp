import { store } from "../state/store.js";

export class PlayerCard extends HTMLElement {
    constructor() {
        super();
        this.currentlyShowingId = null;
        this.isAnimating = false;
    }

    connectedCallback() {
        this.innerHTML = `
            <div class="active-card-container">
                <div class="player-card" id="dynamic-card">
                    <img class="player-card__avatar" id="dyn-avatar" src="assets/avatar/avatar-1.png" alt="Avatar">
                    
                    <div class="player-card__info">
                        <h3 class="player-card__name" id="dyn-name">...</h3>
                        
                        <div class="power-dots" id="dyn-powers"></div>
                    </div>
                </div>
            </div>
        `;

        this.cardEl = this.querySelector('#dynamic-card');
        this.labelEl = this.querySelector('#dyn-label');
        this.nameEl = this.querySelector('#dyn-name');
        this.powersEl = this.querySelector('#dyn-powers');

        store.subscribe('currentTurnPlayerId', (newTurnId) => this.handleTurnChange(newTurnId));
        
        if (store.state.currentTurnPlayerId) {
            this.handleTurnChange(store.state.currentTurnPlayerId);
        }
    }

    handleTurnChange(newTurnId) {
        if (!newTurnId || this.currentlyShowingId === newTurnId || this.isAnimating) return;

        this.isAnimating = true;

        if (!this.currentlyShowingId) {
            this.updateDOM(newTurnId);
            this.cardEl.classList.add('anim-enter');
            this.finalizeAnimation(newTurnId);
            return;
        }

        this.cardEl.classList.remove('anim-enter');
        this.cardEl.classList.add('anim-exit');

        setTimeout(() => {
            this.updateDOM(newTurnId);
            
            this.cardEl.classList.remove('anim-exit');
            this.cardEl.classList.add('anim-enter');
            
            this.finalizeAnimation(newTurnId);
        }, 300);
    }

    updateDOM(playerId) {
        const isMe = playerId === store.state.user.id;
        
        const name = isMe ? store.state.user.name : store.state.opponent.name;

        this.cardEl.classList.remove('theme-orange', 'theme-blue');
        if (isMe) {
            this.cardEl.classList.add('theme-orange');
        } else {
            this.cardEl.classList.add('theme-blue');
        }
        
        this.nameEl.innerText = name;

        const playersList = store.state.players || [];
        const playerData = playersList.find(p => p.id === playerId);
        
        let powerHtml = '';
        if (playerData && playerData.inventory) {
            const powerCount = playerData.inventory.filter(item => item !== null).length;
            for (let i = 0; i < 5; i++) {
                const filledClass = i < powerCount ? 'filled' : 'empty';
                powerHtml += `<span class="dot ${filledClass}"></span>`;
            }
        }
        this.powersEl.innerHTML = powerHtml;
    }

    finalizeAnimation(newId) {
        setTimeout(() => {
            this.isAnimating = false;
            this.currentlyShowingId = newId;
        }, 300);
    }
}

customElements.define("player-card", PlayerCard);