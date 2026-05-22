import { store } from "../../state/store.js";
import { resolveCellStyles } from "./cell.styles.js";
import { handleCellClick } from "./cell.actions.js";

export class CellComponent extends HTMLElement {
    
    getCellData() {
        return {
            x: this.getAttribute('x'),
            y: this.getAttribute('y'),
            letter: this.getAttribute('letter') || '',
            revealed: this.getAttribute('revealed') === 'true',
            revealedBy: this.getAttribute('revealed-by'),
            foundBy: this.getAttribute('found-by'),
            effectType: this.getAttribute('effect-type'),
            effectOwner: this.getAttribute('effect-owner'),
            remainingClicks: this.getAttribute('remaining-clicks')
        };
    }

    getVisualState() {
        const { playerEffects, user } = store.state;
        return {
            user: user,
            isBlind: playerEffects?.blind,
            canDetectTraps: playerEffects?.detect_traps
        };
    }

    connectedCallback() {
        const cell = this.getCellData();
        const state = this.getVisualState();
        
        const ui = resolveCellStyles(cell, state);

        this.className = ui.classes.join(" ");
        this.innerHTML = ui.content;

        this.addEventListener('click', () => handleCellClick(this, cell));
    }
}

customElements.define("cell-component", CellComponent);