import { store } from "../state/store.js";
import "./cell.js";

export class BoardComponent extends HTMLElement {
    connectedCallback() {
        this.className = "board-grid";
        store.subscribe('board', () => this.render());
        store.subscribe('foundCellsMap', () => this.render());
        store.subscribe('playerEffects', () => this.render());
        this.render();
    }

    render() {
        const boardData = store.state.board;
        if (!boardData || boardData.length === 0) return;

        const foundMap = store.state.foundCellsMap || {};
        let html = '';
        
        boardData.forEach((row, X) => {
            row.forEach((cellData, Y) => {
                
                const ownerId = cellData.effect ? cellData.effect.ownerId : '';
                const effectType = cellData.effect ? cellData.effect.effect : '';
                
                const wordOwnerId = foundMap[`${X},${Y}`];

                html += `
                    <cell-component
                        x="${X}" 
                        y="${Y}" 
                        letter="${cellData.letter || ''}" 
                        revealed="${cellData.revealed}"
                        revealed-by="${cellData.revealedBy || ''}" 
                        found-by="${wordOwnerId || ''}" 
                        effect-type="${effectType}"
                        effect-owner="${ownerId}"
                        remaining-clicks="${cellData.effect?.remainingClicks || ''}">
                    </cell-component>
                `;
            });
        });

        this.innerHTML = html;
    }
}
customElements.define("board-component", BoardComponent);