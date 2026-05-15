import { store } from "../state/store.js";

export class WordsComponent extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
            <div class="words-container">
                <div class="words" id="words-list"></div>
            </div>
        `;
        this.listEl = this.querySelector('#words-list');

        store.subscribe('words', (words) => this.render(words));

        if (store.state.words && store.state.words.length > 0) {
            this.render(store.state.words);
        }
    }

    render(words) {
        if (!words) return;

        let html = '';
        words.forEach(w => {
            let classes = "word";
            
            if (w.found) {
                classes += " found";
                
                if (w.foundBy) {
                    classes += w.foundBy === store.state.user.id ? " found-me" : " found-opponent";
                }
            }

            html += `<span class="${classes}">${w.word}</span>`;
        });

        this.listEl.innerHTML = html;
    }
}
customElements.define("words-component", WordsComponent);