class Inventory extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
            
        `;
    }
}

customElements.define("inventory-component", Inventory);