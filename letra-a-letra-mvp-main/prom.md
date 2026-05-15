[CONTEXTO]
Estou desenvolvendo um jogo multiplayer em JavaScript puro, integrado com uma API que controla o estado do jogo.

Existe um sistema de palavras que podem ser encontradas ao revelar células no tabuleiro.

---

[OBJETIVO]
Implementar corretamente a funcionalidade de “encontrar palavra”, incluindo:
- Atualização visual do tabuleiro
- Atualização da lista de palavras
- Diferenciação entre jogador 1 e jogador 2

---

[REGRA DE NEGÓCIO]

Quando uma palavra for encontrada:

1. A API deve informar:
   - Qual palavra foi encontrada
   - Qual jogador encontrou

2. O frontend deve reagir a esse evento

---

[COMPORTAMENTO ESPERADO]

### 1. NO TABULEIRO (CELULAS)

Para cada célula que faz parte da palavra encontrada:

- O fundo da célula deve mudar:
  - Cor do jogador 1 (ex: laranja)
  - Cor do jogador 2 (ex: azul)

- A borda da célula deve:
  - Ter a cor do jogador que encontrou a palavra

Resumo:
👉 Palavra encontrada = célula com fundo + borda na cor do jogador que encontrou

---

### 2. NA LISTA DE PALAVRAS

No componente de palavras:

- A palavra encontrada deve:
  - Ficar riscada (ex: text-decoration: line-through)
  - Ser marcada como “encontrada”

- Opcional (melhoria):
  - Aplicar leve destaque na cor do jogador que encontrou

---

[IMPORTANTE]

- NÃO decidir isso no frontend
- O frontend deve apenas reagir ao estado vindo da API/WebSocket
- Garantir que ambos os jogadores vejam o mesmo resultado

---

[REQUISITOS TÉCNICOS]

- Atualizar o `store` com base no evento da API
- UI deve reagir ao estado (não manipular DOM manualmente sem estado)
- Separar:
  - Lógica (quem encontrou)
  - Estado (store)
  - Renderização (componentes)

---

[PONTOS DE ATENÇÃO]

- Evitar sobrescrever células já encontradas
- Garantir que a cor aplicada seja do jogador correto
- Evitar inconsistência entre tabuleiro e lista de palavras

---

[ENTREGA ESPERADA]

- Lógica para tratar evento de palavra encontrada
- Atualização do estado global
- Atualização do board (células)
- Atualização da lista de palavras
- Garantia de sincronização entre jogadores

---

[OBS]
Foco em um MVP funcional, consistente e sem bugs visuais.

[CÓDIGO]

```
.board-grid {
    display: grid;
    grid-template-columns: repeat(10, 1fr);
    grid-template-rows: repeat(10, 1fr);
    gap: 4px;    
    width: 450px;
    height: 450px;
    margin: 0 auto;
    background-color: rgba(255, 255, 255);
    border: 5px solid black;
    padding: 10px;
    border-radius: 8px;
    box-shadow: 0 10px 30px rgba(0,0,0,0.5);    
    position: relative;
    z-index: 10;
}

.cell {
    background-color: #ffffff;
    border-radius: 4px;
    border: solid black;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 24px;
    font-weight: bold;
    color: white;
    cursor: pointer;
    text-transform: uppercase;
    transition: all 0.2s ease-in-out;
    box-shadow: inset 0 -3px 0 rgba(0,0,0,0.3);
}

.cell.hidden:hover {
    background-color: #34495e;
    transform: scale(1.05);
}

.cell.revealed {
    background-color: #ecf0f1;
    color: #2c3e50;
    cursor: default;
    box-shadow: none;
    transform: scale(0.95);
}
```

```
.words-container {
    width: 470px;
    display: flex;
    justify-content: center;
}

.words {
    width: 100%;
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    align-items: center;
    gap: 0.5rem;
    padding: 0.5rem;
    background: rgba(255, 255, 255, 0.2);
    -webkit-backdrop-filter: blur(10px);
    backdrop-filter: blur(10px);
    border-radius: 12px;
    box-shadow: inset 0 4px 6px rgba(0,0,0,0.1);
    border: 4px solid #dbdbdb;
}

.word {
    padding: 0.5rem 0.5rem;
    margin: 0;
    font-size: 1.8rem;
    text-transform: uppercase;
    color: white;
    font-weight: bold;
    letter-spacing: 1px;
    -webkit-text-stroke: 1px #464646;
    transition: all 0.3s ease;
}

.word[style*="line-through"] {
    color: #a0a0a0;
    -webkit-text-stroke: 0;
    transform: scale(0.95);
}
```

```
import { store } from "../state/store.js";

export class WordsComponent extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
            <div class="words-container" id="words-list"></div>
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
            let classes = "word-item";
            
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
```

```
import { store } from "../state/store.js";
import "./cell.js";

export class BoardComponent extends HTMLElement {
    connectedCallback() {
        this.className = "board-grid";
        store.subscribe('board', (newBoard) => this.render(newBoard));
        this.render(store.state.board);
    }

    render(boardData) {
        if (!boardData || boardData.length === 0) return;

        let html = '';
        
        boardData.forEach((row, X) => {
            row.forEach((cellData, Y) => {
                
                const ownerId = cellData.effect ? cellData.effect.ownerId : '';
                const effectType = cellData.effect ? cellData.effect.effect : '';
                const revealedBy = cellData.revealedBy || '';

                html += `
                    <cell-component
                        x="${X}" 
                        y="${Y}" 
                        letter="${cellData.letter || ''}" 
                        revealed="${cellData.revealed}"
                        revealed-by="${revealedBy}"
                        effect-type="${effectType}"
                        effect-owner="${ownerId}"
                        remaining-clicks="${cellData.effect?.remainingClicks || ''}">
                    </cell-component>
                `;
            });
        });

        this.innerHTML = html;
    }
}
customElements.define("board-component", BoardComponent);
```

```
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
```