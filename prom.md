[CONTEXTO]
Estou desenvolvendo um jogo multiplayer em JavaScript puro com WebSocket, onde os efeitos (como FREEZE e UNFREEZE) vêm da API e são refletidos no estado (`store`).

---

[PROBLEMA]
Implementei a lógica de FREEZE / UNFREEZE, mas ela está parcialmente incorreta:

- Quando o oponente usa FREEZE:
  ✔ Eu perco turnos (correto)

- Quando eu tenho o UNFREEZE:
  ✔ O turno volta para mim (correto)

❌ PROBLEMA:
Mesmo estando congelado e tendo UNFREEZE disponível:
- Eu ainda consigo:
  - Clicar no tabuleiro
  - Usar outros poderes
- Isso está errado

---

[OBJETIVO]
Quero que, quando o jogador estiver sob efeito de FREEZE:

- Ele NÃO possa:
  - Interagir com o tabuleiro
  - Usar outros poderes

- Ele SÓ possa:
  - Usar o poder UNFREEZE (se tiver)

---

[COMPORTAMENTO ESPERADO]

Se `playerEffects.freeze === true`:

- Tabuleiro:
  ❌ Bloqueado (sem clique)

- Inventário:
  ❌ Todos os poderes desativados
  ✔ Apenas UNFREEZE habilitado

---

[REQUISITOS DE IMPLEMENTAÇÃO]

- Controlar isso via estado (store), ex:
```js
playerEffects: {
  freeze: true
}
```
Criar uma lógica central que valide ações:
Ex:
```
if (isFrozen && action !== 'UNFREEZE') {
  bloquear ação;
}
```

[PONTOS PARA ANALISAR NO CÓDIGO]

Onde o estado de FREEZE está sendo armazenado
Onde as ações do jogador são validadas (playTurn, usePower, etc.)
Por que não existe bloqueio condicional baseado em FREEZE
Se a UI está ignorando esse estado

[INSTRUÇÕES]

Quero que você:

Identifique o erro na lógica atual
Mostre exatamente onde bloquear as ações
Sugira uma solução simples e centralizada (sem espalhar ifs pelo código)
Garanta que:
Jogador congelado não consegue agir
Apenas UNFREEZE funciona

[OBS]
Foco em evitar inconsistência e garantir controle total do estado.
Sem overengineering.

[CODIGO]

src/components/cell.js
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

        this.addEventListener('click', () => {
            const isMyTurn = store.state.currentTurnPlayerId === user.id;

            // Fora do turno — feedback de erro
            if (!isMyTurn) {
                this.classList.add('shake-error');
                setTimeout(() => this.classList.remove('shake-error'), 400);
                return;
            }

            if (store.state.playerEffects?.freeze) {
                store.state.notification = { 
                    message: "Você está congelado! 🧊 Use o UNFREEZE ou aguarde.", 
                    type: "me" 
                };
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
src/components/inventory.js
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
            if (!store.state.activePower) return;

            // Lógica do botão DESCARTAR
            if (e.target.id === 'discard-btn') {
                GameService.discardPower(store.state.activePower.id);
                store.state.activePower = null;
                document.body.className = "";
            } 
            // Lógica do novo botão USAR (Apenas Globais)
            else if (e.target.id === 'use-btn') {
                if (store.state.playerEffects?.freeze) {
                    // Se estiver congelado e tentar usar algo diferente de UNFREEZE, bloqueia!
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
                
                html += `
                    <div class="slot has-power power-card ${isSelected} ${isDisabled}"
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
            html += `<div class="active-power-actions" style="display: flex; gap: 10px; justify-content: center; margin-top: 15px;">`;
            
            // Só mostra o botão "Usar" se o poder for de efeito Global
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

            // Clique: seleciona/desselecioan o poder
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
                    // Arrasto para CIMA — ativar poder global
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
                    // Arrasto para BAIXO — descartar
                    GameService.discardPower(powerId);
                    store.state.activePower = null;
                    document.body.className = "";
                }
            };

            // Gestos de swipe
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
    playTurn(x, y, powerId = null, powerName = null) {
        if (store.state.currentTurnPlayerId !== store.state.user.id) return;

        wsManager.send({
            type: "PLAYER_ACTION",
            tokenGameId: getTokenGameId(),
            action: buildActionPayload(x, y, powerId, powerName)
        });
    },

    playGlobalPower(powerId, powerName) {
        console.log(`🎮 [GAME SERVICE] Tentativa de usar poder Global: ${powerName} (ID: ${powerId})`);
        
        console.log(`🔍 [GAME SERVICE] Turno atual: ${store.state.currentTurnPlayerId} | Meu ID: ${store.state.user.id}`);
        
        if (store.state.currentTurnPlayerId !== store.state.user.id) return;

        const offensivePowers = ["FREEZE", "BLIND"];
    
        const target = offensivePowers.includes(powerName) 
            ? store.state.opponent.id 
            : store.state.user.id;

        // 2. Monta o payload incluindo o targetId
        const payload = {
            type: "PLAYER_ACTION",
            tokenGameId: getTokenGameId(),
            action: { 
                type: powerName, 
                actionId: powerId,
                targetId: target // <-- A SOLUÇÃO ESTÁ AQUI!
            }
        };
        
        wsManager.send(payload);

        // wsManager.send({
        //     type: "PLAYER_ACTION",
        //     tokenGameId: getTokenGameId(),
        //     action: { type: powerName, actionId: powerId }
        // });
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
        console.log("📥 [WS RECEBIDO BRUTO]:", msg.event, msg);
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
                    store.state.turnEndsAt = msg.turnEndsAt;
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
                store.state.activePower = null;
                document.body.className = "";

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
        const { event: eventName, data } = internalEvent;
        const targetId = data?.targetPlayerId;
        const isMe = targetId === store.state.user.id;

        // ==========================================
        // PALAVRA ENCONTRADA
        // ==========================================
        if (eventName === "WORD_FOUNDED" || eventName === "WORD_FOUND") {
            const { cells, foundedBy } = data;
            if (!cells || !foundedBy) return;

            const currentMap = store.state.foundCellsMap || {};
            cells.forEach(pos => {
                const key = `${pos.x},${pos.y}`;
                if (!currentMap[key]) currentMap[key] = foundedBy;
            });
            store.state.foundCellsMap = { ...currentMap };

            setTimeout(() => {
                store.state.notification = foundedBy === store.state.user.id
                    ? { message: "Você encontrou uma palavra!", type: "me" }
                    : { message: "O oponente encontrou uma palavra!", type: "opponent" };
            }, 100);

            return;
        }

        // ==========================================
        // EFEITOS DO JOGADOR
        // ==========================================
        if (!isMe) return; // os eventos abaixo só importam se o alvo for você

        const setEffect = (key, value) => {
            store.state.playerEffects = { ...store.state.playerEffects, [key]: value };
        };

        switch (eventName) {
            case "PLAYER_BLINDED":      setEffect('blind', true);         break;
            case "PLAYER_UNBLINDED":    setEffect('blind', false);        break;
            case "PLAYER_FROZEN":       setEffect('freeze', true);        break;
            case "PLAYER_UNFROZEN":     setEffect('freeze', false);       break;
            case "IMMUNITY_APPLdIED":    setEffect('immunity', true);      break;
            case "IMMUNITY_REMOVED":    setEffect('immunity', false);     break;
            case "DETECT_TRAPS_APPLIED":setEffect('detect_traps', true);  break;
            case "DETECT_TRAPS_REMOVED":setEffect('detect_traps', false); break;
            case "SPY_APPLIED":         setEffect('spy', true);           break;
            case "SPY_REMOVED":         setEffect('spy', false);          break;
            case "TRAP_TRIGGERED":
                // ajuste aqui conforme o que o backend enviar junto
                break;
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
