import { store } from "../state/store.js";
import "../components/cell.js";

export class BoardComponent extends HTMLElement {
    connectedCallback() {
        this.className = "board-grid";
        store.subscribe('board', (newBoard) => this.render(newBoard));
        this.render(store.state.board);
    }

    render(boardData) {
        if (!boardData || boardData.length === 0) return;

        let html = '';
        
        boardData.forEach((row, X) => {
            row.forEach((cellData, Y) => {
                
                const ownerId = cellData.effect ? cellData.effect.ownerId : '';
                const effectType = cellData.effect ? cellData.effect.effect : '';
                const revealedBy = cellData.revealedBy || '';

                html += `
                    <cell-component
                        x="${X}" 
                        y="${Y}" 
                        letter="${cellData.letter || ''}" 
                        revealed="${cellData.revealed}"
                        revealed-by="${revealedBy}"
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