src/coponents/cell.js
```
import { store } from "../state/store.js";
import { GameService } from "../services/game/gameService.js";

// Sobrevive aos re-renders — guarda células que já animaram
const animatedCells = new Set();

const GLOBAL_POWERS = ["FREEZE", "UNFREEZE", "BLIND", "LANTERN", "IMMUNITY", "DETECT_TRAPS"];

export class CellComponent extends HTMLElement {
    connectedCallback() {
        const x = this.getAttribute('x');
        const y = this.getAttribute('y');
        const letter = this.getAttribute('letter') || '';
        const revealed = this.getAttribute('revealed') === 'true';
        const revealedBy = this.getAttribute('revealed-by');
        const foundBy = this.getAttribute('found-by');
        const effectType = this.getAttribute('effect-type');
        const effectOwner = this.getAttribute('effect-owner');
        const remainingClicks = this.getAttribute('remaining-clicks');

        const { playerEffects, user } = store.state;
        const isBlind = playerEffects?.blind;
        const canDetectTraps = playerEffects?.detect_traps;
        const isEffectMine = effectOwner === user.id;

        const cellKey = `${x},${y}`;
        let classes = `cell ${revealed ? 'revealed' : 'hidden'}`;
        let innerHtml = `<span>${letter}</span>`;

        // ==========================================
        // REGRAS DE RENDERIZAÇÃO (ordem importa)
        // ==========================================

        if (revealed) {
            // Animação única por célula
            if (!animatedCells.has(cellKey)) {
                classes += " animate-reveal";
                animatedCells.add(cellKey);
            }

            // 1. Blind — sobrescreve tudo
            if (isBlind) {
                classes += " cell-blinded";
                innerHtml = `<span>?</span>`;
            }
            // 2. Palavra encontrada
            else if (foundBy) {
                classes += foundBy === user.id ? " found-me" : " found-opponent";
            }
            // 3. Revelada sem palavra
            else if (revealedBy) {
                classes += revealedBy === user.id ? " revealed-me" : " revealed-opponent";
            }
        }

        // 4. Efeitos de célula (BLOCK, TRAP, SPY)
        if (effectType === "BLOCK") {
            classes += isEffectMine ? " block-me" : " block-opponent";
            innerHtml += `<div class="padlock-icon">🔒 ${remainingClicks}</div>`;
        } else if (effectType === "TRAP") {
            if (isEffectMine) {
                classes += " trap-me";
            } else if (canDetectTraps) {
                classes += " trap-detected";
                innerHtml += `<div class="trap-icon">⚠️</div>`;
            }
        } else if (effectType === "SPY" && isEffectMine) {
            classes += " cell-spied";
            innerHtml = `<span>${letter}</span>`;
        }

        this.className = classes;
        this.innerHTML = innerHtml;

        // ==========================================
        // CLIQUE
        // ==========================================

        this.addEventListener('click', () => {
            const isMyTurn = store.state.currentTurnPlayerId === user.id;

            // Fora do turno — feedback de erro
            if (!isMyTurn) {
                this.classList.add('shake-error');
                setTimeout(() => this.classList.remove('shake-error'), 400);
                return;
            }

            const { activePower } = store.state;

            // Poder ativo do tipo CELL — usa o poder na célula
            if (activePower?.scope === "CELL") {
                GameService.playTurn(x, y, activePower.id, activePower.type);
                store.state.activePower = null;
                document.body.className = "";
                return;
            }

            // Poder ativo do tipo GLOBAL — avisa que não precisa de célula
            if (activePower?.scope === "GLOBAL") {
                store.state.notification = {
                    message: "Este poder não precisa de célula! Arraste para cima.",
                    type: "me"
                };
                return;
            }

            // Jogada normal
            GameService.playTurn(x, y);
        });
    }
}

customElements.define("cell-component", CellComponent);
```
src/coponents/inventory.js
```
import { store } from "../state/store.js";
import { GameService } from "../services/game/gameService.js";

const GLOBAL_POWERS = ["FREEZE", "UNFREEZE", "BLIND", "LANTERN", "IMMUNITY", "DETECT_TRAPS"];

export class InventoryComponent extends HTMLElement {
    connectedCallback() {
        this.className = "inventory-section";

        store.subscribe('players', () => this.render());
        store.subscribe('activePower', () => this.render());

        this.addEventListener('click', (e) => {
            if (e.target.id === 'discard-btn' && store.state.activePower) {
                this.dispatchEvent(new CustomEvent('discard-power', {
                    detail: { powerId: store.state.activePower.id },
                    bubbles: true
                }));
            }
        });

        this.render();
    }

    render() {
        const players = store.state.players;
        if (!players?.length) return;

        const me = players.find(p => p.id === store.state.user.id);
        if (!me) return;

        let html = '<div class="inventory">';

        for (let i = 0; i < 5; i++) {
            const power = me.inventory?.[i];

            if (power) {
                const scope = GLOBAL_POWERS.includes(power.name) ? "GLOBAL" : "CELL";
                const isSelected = store.state.activePower?.id === power.id ? 'selected' : '';
                html += `
                    <div class="slot has-power power-card ${isSelected}"
                         data-id="${power.id}"
                         data-type="${power.name}"
                         data-scope="${scope}">
                        ${power.name}
                    </div>
                `;
            } else {
                html += `<div class="slot"></div>`;
            }
        }

        html += '</div>';

        if (store.state.activePower) {
            html += `<button id="discard-btn" class="discard button">Descartar</button>`;
        }

        this.innerHTML = html;
        this.attachGestures();
    }

    attachGestures() {
        this.querySelectorAll('.power-card').forEach(card => {
            let startY = 0;
            const { id: powerId, type: powerType, scope } = card.dataset;

            // Clique: seleciona/desselecioan o poder
            card.addEventListener('click', () => {
                const isAlreadySelected = store.state.activePower?.id === powerId;

                if (isAlreadySelected) {
                    store.state.activePower = null;
                    document.body.className = "";
                    return;
                }

                store.state.activePower = { id: powerId, type: powerType, scope };
                document.body.className = scope === "CELL" ? "target-cell-mode" : "target-global-mode";
            });

            // Gestos de swipe
            card.addEventListener('touchstart', (e) => {
                startY = e.touches[0].clientY;
            });

            card.addEventListener('touchend', (e) => {
                const diffY = startY - e.changedTouches[0].clientY;

                if (diffY > 50) {
                    // Swipe up — ativar poder
                    if (scope === "GLOBAL") {
                        GameService.playGlobalPower(powerId, powerType);
                        store.state.activePower = null;
                        document.body.className = "";
                    } else {
                        store.state.notification = {
                            message: "Selecione uma célula no tabuleiro!",
                            type: "me"
                        };
                    }
                } else if (diffY < -50) {
                    // Swipe down — descartar
                    GameService.discardPower(powerId);
                    store.state.activePower = null;
                    document.body.className = "";
                }
            });
        });
    }
}

customElements.define("inventory-component", InventoryComponent);
```
src/coponents/board.js
```
import { store } from "../state/store.js";
import "./cell.js";

export class BoardComponent extends HTMLElement {
    connectedCallback() {
        this.className = "board-grid";
        store.subscribe('board', () => this.render());
        store.subscribe('foundCellsMap', () => this.render()); 
        this.render();
    }

    render() {
        const boardData = store.state.board;
        if (!boardData || boardData.length === 0) return;

        const foundMap = store.state.foundCellsMap || {};
        let html = '';
        
        boardData.forEach((row, X) => {
            row.forEach((cellData, Y) => {
                
                const ownerId = cellData.effect ? cellData.effect.ownerId : '';
                const effectType = cellData.effect ? cellData.effect.effect : '';
                
                const wordOwnerId = foundMap[`${X},${Y}`];

                html += `
                    <cell-component
                        x="${X}" 
                        y="${Y}" 
                        letter="${cellData.letter || ''}" 
                        revealed="${cellData.revealed}"
                        revealed-by="${cellData.revealedBy || ''}" 
                        found-by="${wordOwnerId || ''}" 
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
src/state/store.js
```
class ReactiveStore {
    constructor(initialState) {
        this.listeners = {};
        this.state = new Proxy(initialState, {
            set: (target, key, value) => {
                target[key] = value;
                this.notify(key, value);
                return true;
            }
        });
    }

    subscribe(key, callback) {
        if (!this.listeners[key]) this.listeners[key] = [];
        this.listeners[key].push(callback);
    }

    notify(key, value) {
        if (this.listeners[key]) {
            this.listeners[key].forEach(callback => callback(value));
        }
    }
}

export const store = new ReactiveStore({
    currentPage: 'home',
    user: { id: null, token: null, name: null },
    opponent: { id: null, name: '???' },
    tokenGameId: null,
    currentTurnPlayerId: null,
    board: [],
    words: [],
    players: [],
    endGameState: { 
        show: false, 
        title: '', 
        message: '', 
        type: ''
    },
    activePower: null,
    playerEffects: {
        blind: false,
        spy: false,
        freeze: false,
        immunity: false,
        detect_traps: false,
    }
});
```
src/websocket/socket/wsEventHandle.js
```
import { store } from "../../state/store.js";

export const wsEventHandler = {
    handle(msg, userId, callbacks) {
        if (msg.events && Array.isArray(msg.events)) {
            this.processInternalEvents(msg.events);
        }

        if (msg.data?.board) this.syncGameState(msg.data);
        switch (msg.event) {
            case "MATCHMAKING_GAME":
                if (msg.status === "FOUNDED") {
                    const opponent = msg.data.players.find(p => p.id !== userId);
                    store.state.tokenGameId = msg.tokenGameId;
                    store.state.opponent = opponent
                        ? { id: opponent.id, name: opponent.nickname }
                        : { id: null, name: "Desafiante" };
                    this.syncGameState(msg.data);
                    store.state.currentPage = 'found';
                    callbacks.onMatchFound();
                }
                break;

            case "PLAYER_ACTION_RESULT":
            case "TURN_EXPIRED":
                if (msg.data?.currentTurnPlayerId) {
                    store.state.currentTurnPlayerId = msg.data.currentTurnPlayerId;
                    this.updateTurnMessage();
                }
                callbacks.onResetWatchdog();
                break;

            case "GAME_OVER":
                const isWinner = msg.data.winner.id === store.state.user.id;
                store.state.endGameState = {
                    show: true,
                    isWinner: isWinner,
                    title: isWinner ? "🏆 VOCÊ VENCEU!" : "💀 VOCÊ PERDEU!",
                    message: isWinner 
                        ? "Parabéns! Você encontrou mais palavras." 
                        : "O oponente foi melhor dessa vez. Tente novamente!"
                };
                break;

            case "REMOVED_BECAUSE_INACTIVITY":
                store.state.endGameState = {
                    show: true,
                    isWinner: false,
                    title: "💤 DESCONECTADO",
                    message: "Você foi removido por inatividade."
                };
                break;

            case "PARTICIPANT_LEAVE":
            case "PARTICIPANT_DISCONNECTED":

                if (store.state.tokenGameId) {
                    store.state.endGameState = {
                        show: true,
                        isWinner: true,
                        title: "🏃 OPONENTE FUGIU",
                        message: "Você venceu por W.O.! O oponente saiu da partida."
                    };
                }
                break;

            case "ERROR":
                if (msg.message === "player_not_in_game"){
                    store.state.endGameState = {
                        show: true,
                        isWinner: true,
                        title: "🏃 OPONENTE FUGIU",
                        message: "Você venceu! O oponente foi desconectado."
                    };
                }
                break;
        }
    },

    processInternalEvents(events) {
        events.forEach(internalEvent => {
            
            if (internalEvent.event === "WORD_FOUNDED" || internalEvent.event === "WORD_FOUND") {
                const { cells, foundedBy } = internalEvent.data;

                if (!cells || !foundedBy) return;

                const currentMap = store.state.foundCellsMap || {};

                cells.forEach(pos => {
                    const key = `${pos.x},${pos.y}`;
                    if (!currentMap[key]) {
                        currentMap[key] = foundedBy;
                    }
                });

                store.state.foundCellsMap = { ...currentMap };

                setTimeout(() => {
                    if (foundedBy === store.state.user.id) {
                        store.state.notification = { 
                            message: "Você encontrou uma palavra!", 
                            type: "me" 
                        };
                    } else {
                        store.state.notification = { 
                            message: "O oponente encontrou uma palavra!", 
                            type: "opponent" 
                        };
                    }
                }, 100);
            }

            if (internalEvent.event === "EFFECT_APPLIED") {
            const { effectType, targetPlayerId } = internalEvent.data;
            if (targetPlayerId === store.state.user.id) {
                store.state.playerEffects = {
                    ...store.state.playerEffects,
                    [effectType.toLowerCase()]: true
                    };
                }
            }

            if (internalEvent.event === "EFFECT_REMOVED") {
                const { effectType, targetPlayerId } = internalEvent.data;
                if (targetPlayerId === store.state.user.id) {
                    store.state.playerEffects = {
                        ...store.state.playerEffects,
                        [effectType.toLowerCase()]: false
                    };
                }
            }
        });
    },

    syncGameState(data) {

        if (data.board) store.state.board = data.board;
        if (data.words) store.state.words = data.words;
        if (data.players) store.state.players = data.players;
        if (data.currentTurnPlayerId) {
            store.state.currentTurnPlayerId = data.currentTurnPlayerId;
            this.updateTurnMessage();
        }
    },

    updateTurnMessage() {
        const isMyTurn = store.state.currentTurnPlayerId === store.state.user.id;
        store.state.gameMessage = isMyTurn ? "🟢 SEU TURNO" : "🔴 TURNO DO OPONENTE";
    }
};
```
src/game/gameService.js
```
import { store } from "../../state/store.js";
import { wsManager } from "../../websocket/socket/socketManager.js";

const getTokenGameId = () => store.state.tokenGameId;

const buildActionPayload = (x, y, powerId, powerName) =>
    powerId
        ? { type: powerName, actionId: powerId, position: { x, y } }
        : { type: "REVEAL", position: { x, y } };

export const GameService = {
    playTurn(x, y, powerId = null, powerName = null) {
        if (store.state.currentTurnPlayerId !== store.state.user.id) return;

        wsManager.send({
            type: "PLAYER_ACTION",
            tokenGameId: getTokenGameId(),
            action: buildActionPayload(x, y, powerId, powerName)
        });
    },

    playGlobalPower(powerId, powerName) {
        if (store.state.currentTurnPlayerId !== store.state.user.id) return;

        wsManager.send({
            type: "PLAYER_ACTION",
            tokenGameId: getTokenGameId(),
            action: { type: powerName, actionId: powerId }
        });
    },

    discardPower(powerId) {
        wsManager.send({
            type: "DISCARD_POWER",
            tokenGameId: getTokenGameId(),
            powerId
        });
    },

    leaveGame(isClosingTab = false) {
        if (!getTokenGameId()) return;

        wsManager.send({
            type: "LEFT_GAME",
            tokenGameId: getTokenGameId()
        });

        store.state.tokenGameId = null;

        if (!isClosingTab) store.state.currentPage = 'home';
    }
};
```
