import { store } from "../../state/store.js";
import { createPowerDots } from "./playerCard.template.js";
import { mapPlayerCardData } from "./playerCard.mapper.js";
import { animateCardTransition, ANIMATION_DURATION } from "./playerCard.animation.js";

export class PlayerCard extends HTMLElement {
    constructor() {
        super();
        this.currentlyShowingId = null;
        this.isAnimating = false;
        this.timerInterval = null; 
    }

    connectedCallback() {
        this.renderBaseTemplate();
        this.cacheElements();
        this.setupSubscriptions();
        this.timerInterval = setInterval(() => this.updateTimerUI(), 200);
    }

    disconnectedCallback() {
        if (this.unsubscribeTurn) this.unsubscribeTurn();
        if (this.unsubscribeBoard) this.unsubscribeBoard();
        if (this.timerInterval) clearInterval(this.timerInterval);
    }

    renderBaseTemplate() {
        this.innerHTML = `
            <div class="active-card-container">
                <div class="player-card" id="dynamic-card">
                    <img class="player-card__avatar" id="dyn-avatar" src="" alt="Avatar">
                    
                    <div class="player-card__info">
                        <div class="player-card__titles">
                            <span class="player-role" id="dyn-role"></span>
                            <h3 class="player-card__name" id="dyn-name">...</h3>
                        </div>
                        <div class="power-dots" id="dyn-powers"></div>
                    </div>

                    <div class="turn-timer" id="dyn-timer" style="display: none;"></div>
                </div>
            </div>
        `;
    }

    cacheElements() {
        this.cardEl = this.querySelector('#dynamic-card');
        this.nameEl = this.querySelector('#dyn-name');
        this.powersEl = this.querySelector('#dyn-powers');
        this.timerEl = this.querySelector('#dyn-timer');
        this.roleEl = this.querySelector('#dyn-role'); 
    }

    setupSubscriptions() {
        this.unsubscribeTurn = store.subscribe('currentTurnPlayerId', (newTurnId) => {
            this.handleTurnChange(newTurnId);
        });

        this.unsubscribeBoard = store.subscribe('board', () => {
            const currentTurnId = store.state.currentTurnPlayerId;
            if (currentTurnId && this.currentlyShowingId !== currentTurnId) {
                this.handleTurnChange(currentTurnId);
            }
        });
        
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
        
        const isMe = playerId === store.state.user?.id;
        this.roleEl.innerText = isMe ? "VOCÊ" : "OPONENTE";
        
        const avatarEl = this.querySelector('#dyn-avatar');
        if (avatarEl && data.avatar) {
            avatarEl.src = data.avatar;
        }
    }

    finalizeAnimation(newId) {
        setTimeout(() => {
            this.isAnimating = false;
            this.currentlyShowingId = newId;
        }, ANIMATION_DURATION);
    }

    updateTimerUI() {
        if (!this.timerEl) return;

        const endsAt = store.state.turnEndsAt;
        const hasGame = !!store.state.tokenGameId;

        if (!hasGame || !endsAt) {
            this.timerEl.style.display = 'none';
            return;
        }

        this.timerEl.style.display = 'flex';

        const now = Date.now();
        const target = new Date(endsAt).getTime();
        let secondsLeft = Math.max(0, Math.ceil((target - now) / 1000));

        this.timerEl.textContent = secondsLeft;

        if (secondsLeft <= 10 && secondsLeft > 0) {
            this.timerEl.classList.add('timer-warning');
        } else {
            this.timerEl.classList.remove('timer-warning');
        }
        
        if (secondsLeft === 0) {
            this.timerEl.textContent = "0";
            this.timerEl.classList.add('timer-warning');
        }
    }
}

customElements.define("player-card", PlayerCard);