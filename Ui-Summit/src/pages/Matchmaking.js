import { store, getDistinctAvatars } from "../state/store.js";
import { wsManager } from "../websocket/socket/socketManager.js";
import { Selectors } from "../state/selectors.js";

export class MatchmakingPage extends HTMLElement {
    constructor() {
        super();
        this.unsubscribe = null;
        this.timers = [];
    }

    connectedCallback() {
        const myName = Selectors.getCurrentPlayer()?.name || "Você";
        
        document.body.style.backgroundImage = "url('assets/background/buscando.png')";

        let myAvatar = Selectors.getCurrentPlayer()?.avatar;
        if (!myAvatar) {
            const newAvatars = getDistinctAvatars();
            myAvatar = newAvatars.p1;
            if (store.state.user) store.state.user.avatar = newAvatars.p1;
            if (store.state.opponent) store.state.opponent.avatar = newAvatars.p2;
        }

        this.innerHTML = `
            <div class="mm-container">
                <div class="mm-card">

                    <div class="mm-title-badge" id="mm-title-badge">
                        <h2 class="mm-title-text" id="mm-title">Buscando Oponente</h2>
                    </div>
                    <p class="mm-subtitle" id="connection-status">Aguardando oponente...</p>

                    <div class="mm-players-row">

                        <div class="mm-player-card mm-player-card--local">
                            <img class="mm-player-avatar" src="${myAvatar}" alt="Seu Avatar">
                            <div class="mm-name-bar mm-name-bar--local">
                                <span class="found-name">${myName}</span>
                            </div>
                        </div>

                        <span class="mm-vs">VS</span>

                        <div class="mm-player-card mm-player-card--searching" id="mm-opp-card">
                            <div id="mm-spinner-box" class="mm-spinner-wrap">
                                <div class="spinner"></div>
                            </div>
                            <img id="opp-avatar" class="mm-player-avatar"
                                 src="" style="display:none;" alt="Oponente">
                            <div class="mm-name-bar mm-name-bar--searching" id="mm-opp-name-bar">
                                <p class="found-name" id="opponent-name">......</p>
                            </div>
                        </div>

                    </div>

                    <button id="btn-cancel" class="mm-btn-cancel">Cancelar</button>

                </div>
            </div>
        `;

        this.cacheElements();
        this.setupListeners();
        this.setupWebSocket();
        this.setupSubscriptions();
    }

    disconnectedCallback() {
        if (this.unsubscribe) this.unsubscribe();
        this.timers.forEach(clearTimeout);
    }

    cacheElements() {
        this.titleEl    = this.querySelector("#mm-title");
        this.titleBadge = this.querySelector("#mm-title-badge");
        this.spinnerBox = this.querySelector("#mm-spinner-box");
        this.oppAvatar  = this.querySelector("#opp-avatar");
        this.oppName    = this.querySelector("#opponent-name");
        this.statusEl   = this.querySelector("#connection-status");
        this.btnCancel  = this.querySelector("#btn-cancel");
        this.oppCard    = this.querySelector("#mm-opp-card");
        this.oppNameBar = this.querySelector("#mm-opp-name-bar");
    }

    setupListeners() {
        this.btnCancel.addEventListener('click', () => {
            if (this.btnCancel.disabled) return;

            store.state.opponent = { ...store.state.opponent, id: null, name: '???' };
            store.state.opponentName = null;
            store.state.tokenGameId = null;

            if (typeof wsManager.disconnect === 'function') {
                wsManager.disconnect();
            }
            
            store.state.currentPage = 'home';
        });
    }

    setupWebSocket() {
        const { token, id } = Selectors.getCurrentPlayer() || {};
        if (token && id) {
            wsManager.connect(token, id);
        } else {
            store.state.currentPage = 'home';
        }
    }

    setupSubscriptions() {
        this.unsubscribe = store.subscribe('opponent', (opp) => {
            if (opp && opp.id) {
                this.showFoundPlayer(opp.name);
            }
        });
    }

    showFoundPlayer(opponentName) {
        if (this.unsubscribe) {
            this.unsubscribe();
            this.unsubscribe = null;
        }

        document.body.style.backgroundImage = "url('assets/background/versus.png')";

        this.titleEl.innerText = "Oponente Encontrado!";
        this.titleBadge.classList.add("mm-title-badge--found");
        this.oppName.innerText = opponentName;
        this.statusEl.innerText = "Preparando o tabuleiro...";
        this.oppAvatar.src = Selectors.getOpponent()?.avatar || "assets/avatar/avatar-2.png";

        const myAvatarImg = this.querySelector(".mm-player-card--local .mm-player-avatar");
        if (myAvatarImg) {
            myAvatarImg.src = Selectors.getCurrentPlayer()?.avatar || "assets/avatar/avatar-1.png";
        }

        this.oppCard.classList.replace("mm-player-card--searching", "mm-player-card--opponent");
        this.oppNameBar.classList.replace("mm-name-bar--searching", "mm-name-bar--opponent");

        this.spinnerBox.style.display = "none";
        this.oppAvatar.style.display = "block";
        this.oppAvatar.classList.add("anim-pop");

        this.btnCancel.disabled = true;
        this.btnCancel.classList.add("mm-btn-cancel--disabled");

        const t1 = setTimeout(() => {
            this.statusEl.innerText = "Pronto! Começando...";
            const t2 = setTimeout(() => {
                store.state.currentPage = 'game';
            }, 300);
            this.timers.push(t2);
        }, 1200);

        this.timers.push(t1);
    }
}

customElements.define("matchmaking-page", MatchmakingPage);
