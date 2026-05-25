import { GameService } from "../services/game/gameService.js";
import { store } from "../state/store.js";
import { Selectors } from "../state/selectors.js";
import "../components/board/board.js";
import "../components/playerCard/playerCard.js";
import "../components/words.js";
import "../components/inventory/inventory.js";
import "../components/cell/cell.js";

export class GamePage extends HTMLElement {
    constructor() {
        super();
        this.unsubscribes = [];
    }

    connectedCallback() {
        document.body.style.backgroundImage = "url('assets/background/partida.png')";

        this.innerHTML = `
            <div class="game-layout">
                <player-card></player-card>
                
                <words-component></words-component>
                
                <board-component></board-component>
                
                <inventory-component></inventory-component>
                
                <div class="game-footer">
                    <button id="btn-leave" class="btn-leave">Sair</button>
                </div>
                
                <end-modal></end-modal>
                <div id="freeze-overlay" class="freeze-overlay"></div>
                <div id="blind-overlay" class="blind-overlay"></div>
            </div>
        `;

        const blindOverlay = this.querySelector('#blind-overlay');
        const freezeOverlay = this.querySelector('#freeze-overlay');

        this.unsubscribes.push(store.subscribe('playerEffects', (effects) => {
            if (effects?.blind) {
                blindOverlay.classList.add('active');
                document.body.classList.add('global-blind-effect');
            } else {
                blindOverlay.classList.remove('active');
                document.body.classList.remove('global-blind-effect');
            }

            if (effects?.freeze) {
                freezeOverlay.classList.add('active');
            } else {
                freezeOverlay.classList.remove('active');
            }
        }));

        this.querySelector('#btn-leave').addEventListener('click', () => {
            if (confirm("Tem certeza que deseja abandonar a partida? Você receberá uma derrota.")) {
                GameService.leaveGame();
            }
        });

        this.addEventListener('cell-clicked', (e) => {
            const { x, y } = e.detail;
            const power = Selectors.getActivePower(); 
            
            if (power) {
                GameService.playTurn(parseInt(x), parseInt(y), power.id, power.type);
                store.state.activePower = null;
            } else {
                GameService.playTurn(parseInt(x), parseInt(y));
            }
        });

        this.addEventListener('discard-power', (e) => {
            const { powerId } = e.detail;
            GameService.discardPower(powerId);
            store.state.activePower = null;
        });
    }

    disconnectedCallback() {
        this.unsubscribes.forEach(unsubscribe => unsubscribe());
        this.unsubscribes = [];
        const blindOverlay = this.querySelector('#blind-overlay');
        const freezeOverlay = this.querySelector('#freeze-overlay');
        if(blindOverlay) blindOverlay.classList.remove('active');
        if(freezeOverlay) freezeOverlay.classList.remove('active');
        document.body.classList.remove('global-blind-effect');
    }
}
customElements.define("game-page", GamePage);
