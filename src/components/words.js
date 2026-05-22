import { store } from "../state/store.js";

export class WordsComponent extends HTMLElement {
    constructor() {
        super();
        this.unsubscribe = null;
    }

    connectedCallback() {
        this.innerHTML = `
            <div class="words-container">
                <div class="words" id="words-list"></div>
            </div>
        `;
        
        this.listEl = this.querySelector('#words-list');

        this.unsubscribe = store.subscribe('words', (words) => this.render(words));

        if (store.state.words && store.state.words.length > 0) {
            this.render(store.state.words);
        }
    }

    disconnectedCallback() {
        if (this.unsubscribe) {
            this.unsubscribe();
        }
    }

    getWordClass(word) {
        let classes = ["word"];
        
        if (word.found) {
            classes.push("found");
            if (word.foundById) {
                classes.push(word.foundById === store.state.user?.id ? "found-me" : "found-opponent");
            }
        }
        return classes.join(" ");
    }

    render(words) {
        if (!words) return;

        const html = words.map(w => {
            const className = this.getWordClass(w);
            return `<span class="${className}">${w.word}</span>`;
        }).join('');

        this.listEl.innerHTML = html;
    }
}
customElements.define("words-component", WordsComponent);