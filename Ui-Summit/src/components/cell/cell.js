import { store } from "../../state/store.js";
import { resolveCellStyles } from "./cell.styles.js";
import { handleCellClick } from "./cell.actions.js";
import { Selectors } from "../../state/selectors.js";

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
        return {
            user: Selectors.getCurrentPlayer(),
            isBlind: Selectors.isBlind(),
            canDetectTraps: Selectors.canDetectTraps()
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
export function playTrapTriggerAnimation(cellElement) {
    // console.log("🎬 [ANIMAÇÃO TRAP] Tentando tocar na célula:", cellElement);
    if (!cellElement) {
        // console.error("❌ [ANIMAÇÃO TRAP] Falhou! Nenhuma célula foi passada para a função.");
        return;
    }

    const trapImg = document.createElement('img');
    trapImg.src = 'assets/powers/icon-trap-cell.png';
    trapImg.classList.add('trap-img', 'animate-trap-found');
    
    cellElement.appendChild(trapImg);
    
    setTimeout(() => {
        if (cellElement.contains(trapImg)) {
            cellElement.removeChild(trapImg);
        }
    }, 2000);
}
customElements.define("cell-component", CellComponent);