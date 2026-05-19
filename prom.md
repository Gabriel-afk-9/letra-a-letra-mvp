[CONTEXTO]
Estou desenvolvendo um frontend em JavaScript puro que consome uma API orientada a eventos (WebSocket).

Vou enviar:
- A documentação da API (.md)
- Um arquivo com o código atual (.md)

A API é a fonte de verdade. O frontend apenas envia ações e reage aos eventos.

---

[OBJETIVO]
Corrigir o comportamento do poder IMMUNITY, que não está funcionando corretamente contra o FREEZE.

---

[REGRA DE JOGO]

Cenário:
- Jogador 1 usa FREEZE no Jogador 2

Comportamento esperado:

Se o Jogador 2 tiver:
- UNFREEZE → pode usar para remover o FREEZE
- IMMUNITY → também deve remover o FREEZE
- Ambos → pode usar qualquer um dos dois

Além disso:
- IMMUNITY também remove efeitos negativos como BLIND (isso já está funcionando)

---

[PROBLEMA]

Atualmente:

- Jogador 2 consegue selecionar o poder IMMUNITY ✔
- Porém NÃO consegue usar o IMMUNITY para remover o FREEZE ❌
- Apenas o UNFREEZE funciona corretamente

---

[COMPORTAMENTO ERRADO]

- O sistema está permitindo usar UNFREEZE, mas bloqueando IMMUNITY
- Mesmo o IMMUNITY tendo a função de remover efeitos negativos

---

[OBJETIVO DA ANÁLISE]

Quero que você identifique:

1. Por que o IMMUNITY não está sendo aceito como ação válida durante FREEZE
2. Onde está a lógica que restringe os poderes quando o jogador está congelado
3. Se essa lógica está permitindo apenas UNFREEZE (hardcoded ou condição errada)

---

[INSTRUÇÕES]

Quero que você:

1. Encontre exatamente onde essa validação acontece
2. Mostre o erro na lógica atual
3. Corrija de forma simples e clara
4. Garanta que:
- IMMUNITY funcione igual UNFREEZE nesse contexto
- Não quebre outros poderes

---

[IMPORTANTE]

- NÃO inventar comportamento fora da API
- Considerar que IMMUNITY remove efeitos negativos
- Foco em corrigir a lógica de permissão de ação

---

[OBJETIVO FINAL]

Garantir que:

- Jogador congelado só possa usar poderes válidos
- IMMUNITY seja tratado corretamente como defesa contra FREEZE
- O jogo não entre em estado inconsistente

---

[CODIGO]

src/components/board.js
```
import { store } from "../state/store.js";
import "./cell.js";

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

src/components/cell.js
```
import { store } from "../state/store.js";
import { GameService } from "../services/game/gameService.js";

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

        if (revealed) {
            if (!animatedCells.has(cellKey)) {
                classes += " animate-reveal";
                animatedCells.add(cellKey);
            }

            if (isBlind) {
                classes += " cell-blinded";
                innerHtml = `<span>?</span>`;
            }
            else if (foundBy) {
                classes += foundBy === user.id ? " found-me" : " found-opponent";
            }
            else if (revealedBy) {
                classes += revealedBy === user.id ? " revealed-me" : " revealed-opponent";
            }
        }

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

        this.addEventListener('click', () => {
 
            const isFreeze = store.state.playerEffects?.freeze;
            const isNotMyTurn = store.state.currentTurnPlayerId !== user.id;

            if (isNotMyTurn || isFreeze) {
                this.classList.add('shake-error');
                setTimeout(() => this.classList.remove('shake-error'), 400);

                if (isFreeze) {
                    store.state.notification = { message: "Você está congelado! 🧊 Use o UNFREEZE.", type: "me" };
                }
                return;
            }

            const { activePower } = store.state;

            if (activePower?.scope === "CELL") {
                GameService.playTurn(x, y, activePower.id, activePower.type);
                store.state.activePower = null;
                document.body.className = "";
                return;
            }

            if (activePower?.scope === "GLOBAL") {
                store.state.notification = {
                    message: "Este poder não precisa de célula! Arraste para cima.",
                    type: "me"
                };
                return;
            }

            GameService.playTurn(x, y);
        });
    }
}

customElements.define("cell-component", CellComponent);
```

src/components/inventory.js
```
import { store } from "../state/store.js";
import { GameService } from "../services/game/gameService.js";

const GLOBAL_POWERS = ["FREEZE", "UNFREEZE", "BLIND", "LANTERN", "IMMUNITY", "DETECT_TRAPS"];

const POWER_ICONS = {
    "FREEZE": "assets/powers/icon-freeze.png",
    "UNFREEZE": "assets/powers/icon-unfreeze.png",
    "BLIND": "assets/powers/icon-blind.png",
    "LANTERN": "assets/powers/icon-lantern.png",
    "IMMUNITY": "assets/powers/icon-imunity.png",
    "DETECT_TRAPS": "assets/powers/icon-detecttraps.png",
    "BLOCK": "assets/powers/icon-block.png",
    "SPY": "assets/powers/icon-spy.png",
    "TRAP": "assets/powers/icon-trap.png"
};

export class InventoryComponent extends HTMLElement {
    connectedCallback() {
        this.className = "inventory-section";

        store.subscribe('players', () => this.render());
        store.subscribe('activePower', () => this.render());
        store.subscribe('playerEffects', () => {
            if (store.state.playerEffects?.freeze && store.state.activePower?.type !== "UNFREEZE") {
                store.state.activePower = null;
                document.body.className = "";
            }
            this.render();
        });

        this.addEventListener('click', (e) => {
            if (!store.state.activePower) return;

            if (e.target.id === 'discard-btn') {
                GameService.discardPower(store.state.activePower.id);
                store.state.activePower = null;
                document.body.className = "";
            } 
            else if (e.target.id === 'use-btn') {
                if (store.state.playerEffects?.freeze) {
                    if (store.state.activePower.type !== "UNFREEZE") {
                        store.state.notification = { 
                            message: "Você está congelado! Só pode usar o poder UNFREEZE.", 
                            type: "me" 
                        };
                        return;
                    }
                }                
                
                GameService.playGlobalPower(store.state.activePower.id, store.state.activePower.type);
                store.state.activePower = null;
                document.body.className = "";
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
                const isFrozen = store.state.playerEffects?.freeze;
                const isDisabled = (isFrozen && power.name !== "UNFREEZE") ? 'power-disabled' : '';
                //Mudar dps
                const imgSrc = POWER_ICONS[power.name] || "./assets/powers/default.png";
                
                html += `
                    <div class="slot has-power power-card ${isSelected} ${isDisabled}"
                         data-id="${power.id}"
                         data-type="${power.name}"
                         data-scope="${scope}">
                         <img src="${imgSrc}" alt="${power.name}" class="power-icon" draggable="false" />
                    </div>
                `;
            } else {
                html += `<div class="slot"></div>`;
            }
        }

        html += '</div>';

        if (store.state.activePower) {
            html += `<div class="active-power-actions" style="display: flex; gap: 10px; justify-content: center; margin-top: 15px;">`;
            
            if (store.state.activePower.scope === "GLOBAL") {
                html += `<button id="use-btn" class="use button" style="background-color: #409ddb;">Usar Poder</button>`;
            }
            
            html += `<button id="discard-btn" class="discard button" style="background-color: #e74c3c;">Descartar</button>`;
            html += `</div>`;
        }

        this.innerHTML = html;
        this.attachGestures();
    }

    attachGestures() {
        this.querySelectorAll('.power-card').forEach(card => {
            let startY = 0;
            const { id: powerId, type: powerType, scope } = card.dataset;

            card.addEventListener('click', () => {
                if (store.state.playerEffects?.freeze && powerType !== "UNFREEZE") {
                    return; 
                }
                const isAlreadySelected = store.state.activePower?.id === powerId;

                if (isAlreadySelected) {
                    store.state.activePower = null;
                    document.body.className = "";
                    return;
                }

                store.state.activePower = { id: powerId, type: powerType, scope };
                document.body.className = scope === "CELL" ? "target-cell-mode" : "target-global-mode";
            });

            const handleSwipe = (diffY) => {
                if (store.state.playerEffects?.freeze && powerType !== "UNFREEZE") {
                    return; 
                }
                if (diffY > 50) {
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
                    GameService.discardPower(powerId);
                    store.state.activePower = null;
                    document.body.className = "";
                }
            };

            card.addEventListener('touchstart', (e) => {
                startY = e.touches[0].clientY;
            });

            card.addEventListener('touchend', (e) => {
                const diffY = startY - e.changedTouches[0].clientY;
                handleSwipe(diffY);

            });

            card.addEventListener('mousedown', (e) => {
                startY = e.clientY;
            });

            card.addEventListener('mouseup', (e) => {
                const diffY = startY - e.clientY;
                handleSwipe(diffY);
            });
        });
    }
}

customElements.define("inventory-component", InventoryComponent);
```

src/constants/effects.js
```
export const EFFECT_EVENTS = new Set([
    "PLAYER_BLINDED", "PLAYER_USE_LANTERN",
    "PLAYER_FROZEN", "PLAYER_UNFREEZE",
    "IMMUNITY_APPLIED", "IMMUNITY_REMOVED",
    "DETECT_TRAPS_APPLIED", "DETECT_TRAPS_REMOVED",
    "SPY_APPLIED", "SPY_REMOVED"
]);

export const EFFECT_MAP = {
    PLAYER_BLINDED:       { key: 'blind',         value: true  },
    PLAYER_USE_LANTERN:   { key: 'blind',         value: false },
    PLAYER_FROZEN:        { key: 'freeze',        value: true  },
    PLAYER_UNFREEZE:      { key: 'freeze',        value: false },
    IMMUNITY_APPLIED:     { key: 'immunity',      value: true  },
    IMMUNITY_REMOVED:     { key: 'immunity',      value: false },
    DETECT_TRAPS_APPLIED: { key: 'detect_traps',  value: true  },
    DETECT_TRAPS_REMOVED: { key: 'detect_traps',  value: false },
    SPY_APPLIED:          { key: 'spy',           value: true  },
    SPY_REMOVED:          { key: 'spy',           value: false },
};
```

src/services/game/gameService.js
```
import { store } from "../../state/store.js";
import { wsManager } from "../../websocket/socket/socketManager.js";

const getTokenGameId = () => store.state.tokenGameId;

const buildActionPayload = (x, y, powerId, powerName) =>
    powerId
        ? { type: powerName, actionId: powerId, position: { x, y } }
        : { type: "REVEAL", position: { x, y } };

export const GameService = {
    canAct(actionName = "REVEAL") {
        if (store.state.currentTurnPlayerId !== store.state.user.id) return false;

        if (store.state.playerEffects?.freeze && actionName !== "UNFREEZE") {
            store.state.notification = { 
                message: "Ação bloqueada! Você está congelado 🧊.", 
                type: "me" 
            };
            return false;
        }
        return true;
    },
    playTurn(x, y, powerId = null, powerName = null) {
        if (store.state.currentTurnPlayerId !== store.state.user.id) return;

        wsManager.send({
            type: "PLAYER_ACTION",
            tokenGameId: getTokenGameId(),
            action: buildActionPayload(x, y, powerId, powerName)
        });
    },

    playGlobalPower(powerId, powerName) {
        if (!this.canAct(powerName)) return;

        const offensivePowers = ["FREEZE", "BLIND"];
    
        const target = offensivePowers.includes(powerName) 
            ? store.state.opponent.id 
            : store.state.user.id;

        wsManager.send({
            type: "PLAYER_ACTION",
            tokenGameId: getTokenGameId(),
            action: { type: powerName, actionId: powerId, targetId: target }
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
        store.state.playerEffects = {
            blind: false, spy: false, freeze: false, immunity: false, detect_traps: false
        };
        document.body.className = "";

        if (!isClosingTab) store.state.currentPage = 'home';
    }
};
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

src/state/gameActions.js
```
import { store } from "./store.js";

export const GameActions = {
    clearGameState() {
        store.state.activePower = null;
        store.state.pendingUnblind = false;
        store.state.playerEffects = {
            blind: false, spy: false, freeze: false,
            immunity: false, detect_traps: false
        };
        document.body.className = "";
    },

    setEffect(key, value) {
        store.state.playerEffects = { ...store.state.playerEffects, [key]: value };

        if (key === 'freeze') {
            if (value) {
                document.body.classList.add('is-frozen');
            } else {
                document.body.classList.remove('is-frozen');
                store.state.activePower = null;
            }
        }
    },

    syncGameState(data) {
        if (data.board)   store.state.board   = data.board;
        if (data.words)   store.state.words   = data.words;
        if (data.players) store.state.players = data.players;

        if (data.currentTurnPlayerId) {
            store.state.currentTurnPlayerId = data.currentTurnPlayerId;
            this.updateTurnMessage();
        }
    },

    updateTurnMessage() {
        const isMyTurn = store.state.currentTurnPlayerId === store.state.user.id;
        store.state.gameMessage = isMyTurn ? "🟢 SEU TURNO" : "🔴 TURNO DO OPONENTE";
    
        if (isMyTurn) {
            if (store.state.pendingUnfreeze) {
                this.setEffect('freeze', false);
                store.state.pendingUnfreeze = false;
            }

            if (store.state.pendingUnblind) {
                    this.setEffect('blind', false);
                    store.state.pendingUnblind = false;
            }
        }
    },  

    setEndGameState(show, isWinner, title, message) {
        store.state.endGameState = { show, isWinner, title, message };
    }
};
```

src/websocket/handlers/internalEvents.js
```
import { store } from "../../state/store.js";
import { GameActions } from "../../state/gameActions.js";
import { EFFECT_EVENTS, EFFECT_MAP } from "../../constants/effects.js";

const isTargetMe = (data) => {
    if (!data) return false;
    return Object.values(data).includes(store.state.user.id);
};

const handleWordFound = ({ cells, foundedBy }) => {
    if (!cells || !foundedBy) return;

    const currentMap = store.state.foundCellsMap || {};
    cells.forEach(({ x, y }) => {
        currentMap[`${x},${y}`] = foundedBy;
    });
    store.state.foundCellsMap = { ...currentMap };

    setTimeout(() => {
        store.state.notification = foundedBy === store.state.user.id
            ? { message: "Você encontrou uma palavra!", type: "me" }
            : { message: "O oponente encontrou uma palavra!", type: "opponent" };
    }, 100);
};

export const processInternalEvents = (events) => {
    events.forEach(({ event: eventName, data }) => {
        console.log(`🚨 [DEBUG EFEITO] Nome do Evento: ${eventName} | Dados:`, data);
        if (eventName === "WORD_FOUNDED" || eventName === "WORD_FOUND") {
            handleWordFound(data);
            return;
        }

        if (eventName === "TURN_PASSED" && isTargetMe(data)) {
            
            
            if (store.state.playerEffects?.freeze) {
                                store.state.freezeTurnsLeft = (store.state.freezeTurnsLeft || 3) -1;
                
                
                if (store.state.freezeTurnsLeft <= 0) {
                    store.state.pendingUnfreeze = true;
                }
            }

            return; 
        }

        if (eventName === "CELL_REVEALED" && data?.revealedBy === store.state.user.id) {
            if (store.state.playerEffects?.blind) {
                store.state.blindTurnsLeft = (store.state.blindTurnsLeft || 3) - 1;
                
                if (store.state.blindTurnsLeft <= 0) {
                    store.state.pendingUnblind = true; 
                }            
            }
            
        }

        if (EFFECT_EVENTS.has(eventName) && isTargetMe(data)) {
            const effect = EFFECT_MAP[eventName];
            if (effect) GameActions.setEffect(effect.key, effect.value);
        }

        if (eventName === "PLAYER_FROZEN") {
                store.state.freezeTurnsLeft = 3;
                store.state.pendingUnfreeze = false;
        }

        if (eventName === "PLAYER_BLINDED") {
                store.state.blindTurnsLeft = 3;
                store.state.pendingUnblind = false;
        }
    });
};
```

src/websocket/socket/wsEventHandler.js
```
import { store } from "../../state/store.js";
import { GameActions } from "../../state/gameActions.js";
import { processInternalEvents } from "../handlers/internalEvents.js";

export const wsEventHandler = {
    handle(msg, userId, callbacks) {
        if (Array.isArray(msg.events)) {
            processInternalEvents(msg.events);
        }

        if (msg.data?.board) {
            GameActions.syncGameState(msg.data);
        }

        this.routeEvent(msg, userId, callbacks);
    },

    routeEvent(msg, userId, callbacks) {
        switch (msg.event) {
            case "MATCHMAKING_GAME":
                if (msg.status !== "FOUNDED") break;
                const opponent = msg.data.players.find(p => p.id !== userId);
                store.state.tokenGameId = msg.tokenGameId;
                store.state.opponent = opponent
                    ? { id: opponent.id, name: opponent.nickname }
                    : { id: null, name: "Desafiante" };
                store.state.currentPage = 'found';
                callbacks.onMatchFound();
                break;

            case "PLAYER_ACTION_RESULT":
            case "TURN_EXPIRED":
                if (msg.data?.currentTurnPlayerId) {
                    store.state.currentTurnPlayerId = msg.data.currentTurnPlayerId;
                    store.state.turnEndsAt = msg.turnEndsAt;
                    GameActions.updateTurnMessage();
                }
                callbacks.onResetWatchdog();
                break;

            case "GAME_OVER": {
                GameActions.clearGameState();
                const isWinner = msg.data.winner.id === store.state.user.id;
                GameActions.setEndGameState(
                    true, isWinner,
                    isWinner ? "🏆 VOCÊ VENCEU!" : "💀 VOCÊ PERDEU!",
                    isWinner ? "Parabéns! Você encontrou mais palavras."
                             : "O oponente foi melhor dessa vez. Tente novamente!"
                );
                break;
            }

            case "REMOVED_BECAUSE_INACTIVITY":
                GameActions.clearGameState();
                GameActions.setEndGameState(
                    true, false, "💤 DESCONECTADO",
                    "Você foi removido por inatividade."
                );
                break;

            case "PARTICIPANT_LEAVE":
            case "PARTICIPANT_DISCONNECTED":
                if (!store.state.tokenGameId) break;
                GameActions.clearGameState();
                GameActions.setEndGameState(
                    true, true, "🏃 OPONENTE FUGIU",
                    "Você venceu por W.O.! O oponente saiu da partida."
                );
                break;

            case "ERROR":
                store.state.activePower = null;
                document.body.className = "";
                if (msg.message === "player_not_in_game") {
                    GameActions.clearGameState();
                    GameActions.setEndGameState(
                        true, true, "🏃 OPONENTE FUGIU",
                        "Você venceu! O oponente foi desconectado."
                    );
                }
                break;
        }
    }
};
```