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
                <div id="immunity-overlay" class="immunity-overlay"></div>

                <div id="confirm-leave-overlay" class="end-overlay hidden loser" style="z-index: 10001;">
                    <div class="end-modal">
                        <h1>⚠️ ATENÇÃO</h1>
                        <p>Tem certeza que deseja abandonar a partida?<br>Você receberá uma <b>derrota automática</b>.</p>
                        <div style="display: flex; gap: 15px; justify-content: center; flex-wrap: wrap; margin-top: 20px;">
                            <button id="btn-cancel-leave" style="background-color: #95a5a6 !important;">Voltar ao Jogo</button>
                            <button id="btn-confirm-leave">Sim, Abandonar</button>
                        </div>
                    </div>
                </div>
            </div>
        `;

        const blindOverlay = this.querySelector('#blind-overlay');
        const freezeOverlay = this.querySelector('#freeze-overlay');
        const immunityOverlay = this.querySelector('#immunity-overlay');
        
        // Elementos do novo Modal
        const confirmOverlay = this.querySelector('#confirm-leave-overlay');
        const btnCancelLeave = this.querySelector('#btn-cancel-leave');
        const btnConfirmLeave = this.querySelector('#btn-confirm-leave');

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

            if (effects?.immunity) {
                immunityOverlay.classList.add('active');
            } else {
                immunityOverlay.classList.remove('active');
            }
        }));

        this.querySelector('#btn-leave').addEventListener('click', () => {
            confirmOverlay.classList.replace('hidden', 'show');
        });

        btnCancelLeave.addEventListener('click', () => {
            confirmOverlay.classList.replace('show', 'hidden');
        });

        btnConfirmLeave.addEventListener('click', () => {
            confirmOverlay.classList.replace('show', 'hidden'); 
            GameService.leaveGame();
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
        const immunityOverlay = this.querySelector('#immunity-overlay');
        if(blindOverlay) blindOverlay.classList.remove('active');
        if(freezeOverlay) freezeOverlay.classList.remove('active');
        if(immunityOverlay) immunityOverlay.classList.remove('active');
        document.body.classList.remove('global-blind-effect');
    }
}
customElements.define("game-page", GamePage);