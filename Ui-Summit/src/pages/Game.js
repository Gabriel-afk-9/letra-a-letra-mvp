import { GameService } from "../services/gameService.js";
import { store } from "../state/store.js";
import "../components/board.js";
import "../components/player.js";
import "../components/words.js";
import "../components/inventory.js";
import "../components/active-player.js";

export class GamePage extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
            <div class="game-layout">
                <active-player-card></active-player-card>
                <words-component></words-component>
                <board-component></board-component>
                <inventory-component></inventory-component>
            </div>
        `;

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