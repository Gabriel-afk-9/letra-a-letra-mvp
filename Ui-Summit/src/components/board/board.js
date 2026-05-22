import { store } from "../../state/store.js";
import { mapBoardCells } from "./board.mapper.js";
import { createCell } from "./board.template.js";
import "../cell/cell.js"; 

export class BoardComponent extends HTMLElement {
    connectedCallback() {
        this.className = "board-grid";
        
        store.subscribe('board', () => this.render());
        store.subscribe('foundCellsMap', () => this.render());
        store.subscribe('playerEffects', () => this.render());
        
        this.render();
    }

    render() {
        const boardData = store.state.board;
        if (!boardData || boardData.length === 0) return;

        const foundMap = store.state.foundCellsMap || {};
        
        const cells = mapBoardCells(boardData, foundMap);

        this.innerHTML = cells.map(createCell).join("");
    }
}

customElements.define("board-component", BoardComponent);