class Player extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
            <div></div>
        `
    }
}

customElements.define("player-component", Player);