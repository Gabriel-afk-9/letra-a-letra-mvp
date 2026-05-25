import { store } from "../../state/store.js";
import { mapBoardCells } from "./board.mapper.js";
import { createCell } from "./board.template.js";
import { playTrapTriggerAnimation } from "../cell/cell.js"; 

export class BoardComponent extends HTMLElement {
    constructor() {
        super();
        this.unsubscribes = [];
    }

    connectedCallback() {
        this.className = "board-grid";
        
        this.unsubscribes.push(store.subscribe('board', () => this.render()));
        this.unsubscribes.push(store.subscribe('foundCellsMap', () => this.render()));
        this.unsubscribes.push(store.subscribe('playerEffects', () => this.render()));
        this.unsubscribes.push(store.subscribe('cellAnimation', (animation) => this.playCellAnimation(animation)));
        
        this.unsubscribes.push(store.subscribe('activePower', () => {
            const power = store.state.activePower;
            
            const boardGrid = this.querySelector('.board-grid') || this.querySelector('.board-container') || this;
            
            boardGrid.classList.remove('board-pulsing', 'board-disabled');

            if (power) {
                if (power.scope === 'GLOBAL') {
                    boardGrid.classList.add('board-disabled');
                } else {
                    boardGrid.classList.add('board-pulsing');
                }
            }
        }));
        this.render();
    }

    render() {
        const boardData = store.state.board;
        if (!boardData || boardData.length === 0) return;

        const foundMap = store.state.foundCellsMap || {};
        
        const cells = mapBoardCells(boardData, foundMap);

        this.innerHTML = cells.map(createCell).join("");
    }

    playCellAnimation(animation) {
        if (!animation || animation.type !== "trap") return;
        if (animation.x === undefined || animation.y === undefined) return;

        setTimeout(() => {
            const cellEl = this.querySelector(
                `cell-component[x="${animation.x}"][y="${animation.y}"]`
            );
            if (cellEl) {
                playTrapTriggerAnimation(cellEl);
            }
        }, 30);
    }

    disconnectedCallback() {
        this.unsubscribes.forEach(unsubscribe => unsubscribe());
        this.unsubscribes = [];
    }
}

customElements.define("board-component", BoardComponent);
