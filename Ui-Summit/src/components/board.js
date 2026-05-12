import { store } from "../state/store.js";
// Se você for usar um componente cell, importe-o. 
// Caso contrário, pode renderizar as divs direto aqui.

export class BoardComponent extends HTMLElement {
    connectedCallback() {
        this.className = "board-grid";
        
        // Sempre que o WebSocket alterar o 'board' na Store, essa função roda!
        store.subscribe('board', (newBoard) => {
            this.render(newBoard);
        });

        // Tenta renderizar assim que entra na página (caso o board já tenha chegado no matchmaking)
        this.render(store.state.board);
    }

    render(boardData) {
        if (!boardData || boardData.length === 0) {
            this.innerHTML = `<p>Aguardando tabuleiro...</p>`;
            return;
        }

        let html = '';
        
        // O board que vem da API é um Array de Arrays (linhas e colunas)
        boardData.forEach((row, y) => {
            row.forEach((cellData, x) => {
                // Desenha a célula. Se estiver revelada, mostra a letra.
                const content = cellData.revealed ? cellData.letter : '';
                const cellClass = cellData.revealed ? 'cell revealed' : 'cell hidden';
                
                html += `
                    <div class="${cellClass}" data-x="${x}" data-y="${y}">
                        ${content}
                    </div>
                `;
            });
        });

        this.innerHTML = html;
    }
}
customElements.define("board-component", BoardComponent);