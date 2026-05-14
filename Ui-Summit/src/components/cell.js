export class CellComponent extends HTMLElement {
    connectedCallback() {
        const x = this.getAttribute('x');
        const y = this.getAttribute('y');
        const letter = this.getAttribute('letter') || '';
        const revealed = this.getAttribute('revealed') === 'true';

        this.className = `cell ${revealed ? 'revealed' : 'hidden'}`;
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