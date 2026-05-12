class Words extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
        
        `
    }
}

customElements.define("words-component", Words);