import { store } from "../../state/store.js";
import { createPowerDots } from "./playerCard.template.js";
import { mapPlayerCardData } from "./playerCard.mapper.js";
import { animateCardTransition, ANIMATION_DURATION } from "./playerCard.animation.js";

export class PlayerCard extends HTMLElement {
    constructor() {
        super();
        this.currentlyShowingId = null;
        this.isAnimating = false;
    }

    connectedCallback() {
        this.renderBaseTemplate();
        this.cacheElements();
        this.setupSubscriptions();
    }

    renderBaseTemplate() {
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
    }

    cacheElements() {
        this.cardEl = this.querySelector('#dynamic-card');
        this.nameEl = this.querySelector('#dyn-name');
        this.powersEl = this.querySelector('#dyn-powers');
    }

    setupSubscriptions() {
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

        animateCardTransition(this.cardEl, () => {
            this.updateDOM(newTurnId);
            this.finalizeAnimation(newTurnId);
        });
    }

    updateDOM(playerId) {
        const data = mapPlayerCardData(playerId, store.state);

        this.cardEl.className = `player-card ${data.theme}`;
        this.nameEl.innerText = data.name || '...';
        this.powersEl.innerHTML = createPowerDots(data.powerCount);
    }

    finalizeAnimation(newId) {
        setTimeout(() => {
            this.isAnimating = false;
            this.currentlyShowingId = newId;
        }, ANIMATION_DURATION);
    }
}

customElements.define("player-card", PlayerCard);