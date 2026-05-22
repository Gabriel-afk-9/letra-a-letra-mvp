import { GameService } from "../services/game/gameService.js";
import { store } from "../state/store.js";
import "../components/board/board.js";
import "../components/playerCard/playerCard.js";
import "../components/words.js";
import "../components/inventory/inventory.js";
import "../components/cell/cell.js";

export class GamePage extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
            <div class="game-layout">
            <div style="width: 100%; display: flex; justify-content: flex-end;">
                    <button id="btn-leave" class="discard button" style="height: 35px; font-size: 14px; margin: 0;">Sair da Partida</button>
                </div>
                <player-card></player-card>
                <words-component></words-component>
                <board-component></board-component>
                <inventory-component></inventory-component>
                
                <end-modal></end-modal>
            </div>
        `;

        this.querySelector('#btn-leave').addEventListener('click', () => {
            if (confirm("Tem certeza que deseja abandonar a partida? Você receberá uma derrota.")) {
                GameService.leaveGame();
            }
        });

        this.addEventListener('cell-clicked', (e) => {
            const { x, y } = e.detail;
            const power = store.state.selectedPower; 
            
            if (power) {
                GameService.playTurn(parseInt(x), parseInt(y), power.id, power.name);
                store.state.selectedPower = null;
            } else {
                GameService.playTurn(parseInt(x), parseInt(y));
            }
        });

        this.addEventListener('discard-power', (e) => {
            const { powerId } = e.detail;
            GameService.discardPower(powerId);
            store.state.selectedPower = null;
        });
    }
}
customElements.define("game-page", GamePage);