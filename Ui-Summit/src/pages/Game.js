// class GamePage extends HTMLElement {
//     connectedCallback() {
//         this.innerHTML = `
//             <player-component></player-component>
//             <words-component></words-component>
//             <board-component></board-component>
//             <inventory-component></inventory-component>
//         `;
//     }
// }

// customElements.define("game-page", GamePage);

import { GameService } from "../services/gameService.js";
import "../components/board.js";
import "../components/player.js";

export class GamePage extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
            <div class="game-layout">
                <player-component side="left"></player-component>
                <div class="center-area">
                    <board-component></board-component>
                </div>
                <player-component side="right"></player-component>
            </div>
        `;

        this.addEventListener('cell-clicked', (e) => {
            const { x, y } = e.detail;
            GameService.playTurn(parseInt(x), parseInt(y));
        });
    }
}
customElements.define("game-page", GamePage);