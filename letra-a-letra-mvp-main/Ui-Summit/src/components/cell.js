import { store } from "../state/store.js";

export class CellComponent extends HTMLElement {
    connectedCallback() {
        const x = this.getAttribute('x');
        const y = this.getAttribute('y');
        const letter = this.getAttribute('letter') || '';
        const revealed = this.getAttribute('revealed') === 'true';
        const revealedBy = this.getAttribute('revealed-by');

        let classes = `cell ${revealed ? 'revealed' : 'hidden'}`;

        // Lógica: Aplica cor de fundo e borda baseada em quem revelou a célula
        if (revealed && revealedBy) {
            if (revealedBy === store.state.user.id) {
                classes += " cell-me"; // Laranja
            } else {
                classes += " cell-opponent"; // Azul
            }
        }

        this.className = classes;
        this.innerHTML = `<span>${letter}</span>`;

        this.addEventListener('click', () => {
            this.dispatchEvent(new CustomEvent('cell-clicked', {
                detail: { x, y },
                bubbles: true
            }));
        });
    }
}
customElements.define("cell-component", CellComponent);