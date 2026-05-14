import { store } from "../state/store.js";

export class WordsComponent extends HTMLElement {
    connectedCallback() {
        this.className = "words-container";
        
        store.subscribe('words', (newWords) => this.render(newWords));
        
        this.render(store.state.words);
    }

    render(words) {
        if (!words || words.length === 0) {
            this.innerHTML = `<div class="words"><p>Aguardando palavras...</p></div>`;
            return;
        }

        let html = '<div class="words">';
        
        words.forEach(w => {
            const isFound = w.found ? 'text-decoration: line-through; opacity: 0.5;' : '';
            
            html += `
                <p class="word" style="${isFound}">
                    ${w.word}
                </p>
            `;
        });
        
        html += '</div>';
        this.innerHTML = html;
    }
}
customElements.define("words-component", WordsComponent);