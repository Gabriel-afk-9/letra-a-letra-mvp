src/components
board.js
```javascript
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
        
        // CORREÇÃO CRÍTICA: Baseado no seu backend, 'apiX' é a linha, 'apiY' é a coluna
        boardData.forEach((row, apiX) => {
            row.forEach((cellData, apiY) => {
                
                // Mapeia o dono do bloco para sabermos qual cor pintar
                const ownerId = cellData.effect ? cellData.effect.ownerId : '';
                const effectType = cellData.effect ? cellData.effect.effect : '';
                const revealedBy = cellData.revealedBy || '';

                html += `
                    <cell-component 
                        x="${apiX}" 
                        y="${apiY}" 
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

src/components
words.js
```javascript
import { store } from "../state/store.js";

export class WordsComponent extends HTMLElement {
    connectedCallback() {
        this.className = "words-container";
        
        // Assina as mudanças do estado 'words'
        store.subscribe('words', (newWords) => this.render(newWords));
        
        // Renderização inicial
        this.render(store.state.words);
    }

    render(words) {
        if (!words || words.length === 0) {
            this.innerHTML = `<div class="words"><p>Aguardando palavras...</p></div>`;
            return;
        }

        let html = '<div class="words">';
        
        words.forEach(w => {
            // Se a palavra foi encontrada (baseado na API do seu req.md), aplica estilo riscado
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
```

src/components
player.js
```javascript
import { store } from "../state/store.js";

export class PlayerCardComponent extends HTMLElement {
    constructor() {
        super();
        this.isLocal = this.getAttribute('is-local') === 'true';
        
        // Definição das identidades visuais solicitadas
        this.theme = {
            color: this.isLocal ? '#f39c12' : '#3498db', // Laranja (Local) | Azul (Oponente)
            defaultName: this.isLocal ? 'Você' : 'Oponente',
            activeText: this.isLocal ? 'Sua vez' : 'Vez do oponente'
        };
    }

    connectedCallback() {
        // Renderização inicial do esqueleto (DOM)
        this.renderBase();

        // Inscreve o componente nas mudanças da Store
        store.subscribe('currentTurnPlayerId', () => this.updateTurnState());
        store.subscribe('players', () => this.updatePlayerData());
        store.subscribe('user', () => this.updatePlayerData());
        store.subscribe('opponent', () => this.updatePlayerData());

        // Força a primeira atualização com os dados atuais
        this.updatePlayerData();
        this.updateTurnState();
    }

    renderBase() {
        this.innerHTML = `
            <div class="player-card" style="--card-color: ${this.theme.color}">
                <img class="player-card__avatar" src="../../public/avatar-placeholder.png" alt="Avatar">
                
                <div class="player-card__info">
                    <span class="player-card__turn-text" id="turn-badge">Aguardando...</span>
                    <h3 class="player-card__name" id="player-name">${this.theme.defaultName}</h3>
                    
                    <div class="power-dots" id="power-container">
                        </div>
                </div>
            </div>
        `;

        // Cache dos elementos para evitar manipulação pesada de DOM depois
        this.cardEl = this.querySelector('.player-card');
        this.turnBadgeEl = this.querySelector('#turn-badge');
        this.nameEl = this.querySelector('#player-name');
        this.powerContainerEl = this.querySelector('#power-container');
    }

    // ==========================================
    // SEPARAÇÃO: ATUALIZAÇÃO DE ESTADO/DADOS
    // ==========================================
    updatePlayerData() {
        // Descobre qual ID este card representa com base na propriedade is-local
        const playerId = this.isLocal ? store.state.user.id : store.state.opponent?.id;
        if (!playerId) return;

        // Atualiza o Nome
        const name = this.isLocal ? store.state.user.name : store.state.opponent.name;
        if (name) this.nameEl.innerText = name;

        // Atualiza os Poderes (Bolinhas)
        const playersList = store.state.players || [];
        const playerData = playersList.find(p => p.id === playerId);
        
        if (playerData && playerData.inventory) {
            // Conta quantos itens não são nulos no inventário (quantos poderes possui)
            const powerCount = playerData.inventory.filter(item => item !== null).length;
            this.updatePowerDots(powerCount, 5); // Assumindo 5 slots máximos
        }
    }

    updateTurnState() {
        const currentTurnId = store.state.currentTurnPlayerId;
        const myId = this.isLocal ? store.state.user.id : store.state.opponent?.id;

        if (!currentTurnId || !myId) return;

        const isMyTurn = currentTurnId === myId;

        if (isMyTurn) {
            this.cardEl.classList.remove('inactive');
            this.cardEl.classList.add('active');
            this.turnBadgeEl.innerText = this.theme.activeText;
            this.turnBadgeEl.style.opacity = '1';
        } else {
            this.cardEl.classList.remove('active');
            this.cardEl.classList.add('inactive');
            this.turnBadgeEl.innerText = 'Aguardando...';
            this.turnBadgeEl.style.opacity = '0.5';
        }
    }

    // ==========================================
    // SEPARAÇÃO: RENDERIZAÇÃO DA UI MENOR (UX)
    // ==========================================
    updatePowerDots(filledCount, maxSlots) {
        let dotsHtml = '';
        for (let i = 0; i < maxSlots; i++) {
            // Adiciona a classe 'filled' apenas nas bolinhas disponíveis
            const stateClass = i < filledCount ? 'filled' : 'empty';
            dotsHtml += `<span class="dot ${stateClass}"></span>`;
        }
        this.powerContainerEl.innerHTML = dotsHtml;
    }
}

customElements.define("player-card", PlayerCardComponent);
```

src/components
inventory.js
```javascript
import { store } from "../state/store.js";

export class InventoryComponent extends HTMLElement {
    connectedCallback() {
        this.className = "inventory-section";
        
        // Garante que o estado tenha a chave de poder selecionado
        if (store.state.selectedPower === undefined) store.state.selectedPower = null;

        // Reage quando os jogadores (e seus inventários) mudam
        store.subscribe('players', (players) => this.render(players));
        
        // Reage quando a seleção de poder muda (para atualizar a UI)
        store.subscribe('selectedPower', () => this.render(store.state.players));

        // Lida com cliques nos slots e no botão de descartar
        this.addEventListener('click', (e) => {
            const slot = e.target.closest('.slot.has-power');
            if (slot) {
                const powerId = slot.dataset.powerId;
                const powerName = slot.dataset.powerName;
                
                // Se clicar no que já tá selecionado, desmarca. Se não, marca.
                if (store.state.selectedPower?.id === powerId) {
                    store.state.selectedPower = null;
                } else {
                    store.state.selectedPower = { id: powerId, name: powerName };
                }
            }

            if (e.target.id === 'discard-btn') {
                // Emite evento para a GamePage lidar com a regra de negócio
                this.dispatchEvent(new CustomEvent('discard-power', {
                    detail: { powerId: store.state.selectedPower.id },
                    bubbles: true
                }));
            }
        });

        this.render(store.state.players);
    }

    render(players) {
        if (!players || players.length === 0) return;

        // Acha o inventário do próprio usuário
        const me = players.find(p => p.id === store.state.user.id);
        if (!me) return;

        let html = '<div class="inventory">';
        
        // O jogo tem 5 slots fixos
        for (let i = 0; i < 5; i++) {
            const power = me.inventory && me.inventory[i];
            
            if (power) {
                const isSelected = store.state.selectedPower?.id === power.id ? 'selected' : '';
                html += `
                    <div class="slot has-power ${isSelected}" 
                         data-power-id="${power.id}" 
                         data-power-name="${power.name}">
                        ${power.name}
                    </div>
                `;
            } else {
                html += `<div class="slot"></div>`; // Slot vazio
            }
        }
        html += '</div>';

        // Só exibe o botão de descartar se houver um poder selecionado
        if (store.state.selectedPower) {
            html += `<button id="discard-btn" class="discard button">Descartar</button>`;
        }

        this.innerHTML = html;
    }
}
customElements.define("inventory-component", InventoryComponent);
```
src/pages
Game.js
```javascript
import { GameService } from "../services/gameService.js";
import { store } from "../state/store.js";
import "../components/board.js";
import "../components/player.js";
import "../components/words.js";
import "../components/inventory.js";

export class GamePage extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
            <div class="game-layout">
                <player-card is-local="false"></player-card>
                <player-card is-local="true"></player-card>
                <words-component></words-component>
                <board-component></board-component>
                <inventory-component></inventory-component>
            </div>
        `;

        this.addEventListener('cell-clicked', (e) => {
            const { x, y } = e.detail;
            const power = store.state.selectedPower; 
            
            // Se tiver um poder selecionado, joga o poder. Se não, faz o REVEAL normal.
            if (power) {
                GameService.playTurn(parseInt(x), parseInt(y), power.id, power.name);
                store.state.selectedPower = null; // Limpa a seleção após usar
            } else {
                GameService.playTurn(parseInt(x), parseInt(y));
            }
        });

        // Lida com o descarte de cartas vindo do inventory
        this.addEventListener('discard-power', (e) => {
            const { powerId } = e.detail;
            GameService.discardPower(powerId);
            store.state.selectedPower = null;
        });
    }
}
customElements.define("game-page", GamePage);
```
 
src/pages
Matchmaking.js
```javascript

import { store } from "../state/store.js";
import { wsManager } from "../websocket/socket.js";

class MatchmakingPage extends HTMLElement {
    connectedCallback() {
        // Pega o nome do próprio usuário que está salvo na Store
        const myName = store.state.user.name || "Você";

        // Deixamos a div mm-found oculta por padrão usando style
        this.innerHTML = `
            <div class="matchmaking-container">
                <h1 id="mm-title">Buscando Oponente...</h1>
                <div id="mm-spinner" class="spinner"></div>
                
                <div id="mm-found" style="display: none;">
                    <h2 style="color: #4CAF50;">Oponente Encontrado!</h2>
                    <p class="vs-text">
                        <strong>${myName}</strong> <span style="color: red;">VS</span> <strong id="opponent-name">???</strong>
                    </p>
                    <p>Preparando a partida...</p>
                </div>
            </div>
        `;

        // 1. Inicia a conexão WS automaticamente ao carregar a página
        const { token, id } = store.state.user;
        if (token && id) {
            wsManager.connect(token, id);
        } else {
            console.error("Faltam dados do usuário para o WebSocket!");
            store.state.currentPage = 'home';
        }

        // 2. Escuta mudanças na Store
        // Assim que o socket mudar o 'opponentName', essa função roda.
        store.subscribe('opponentName', (newName) => {
            if (newName && newName !== '???') {
                // Esconde os elementos de busca
                this.querySelector("#mm-title").style.display = "none";
                this.querySelector("#mm-spinner").style.display = "none";
                
                // Mostra a tela de VS
                const foundDiv = this.querySelector("#mm-found");
                foundDiv.style.display = "block";
                
                // Atualiza o texto do oponente
                this.querySelector("#opponent-name").innerText = newName;
            }
        });
    }
}
customElements.define("matchmaking-page", MatchmakingPage);
```
src/scripts
script.js
```javascript
import "../components/player.js";
import "../components/words.js";
import "../components/board.js";
import "../components/inventory.js";
import "../pages/Home.js";
import "../pages/Game.js";
import "../pages/Matchmaking.js";

// Importa a Store
import { store } from "../state/store.js";

const root = document.body; // ou uma div id="app"

// O Roteador baseado em Estado:
// Sempre que `store.state.currentPage` for alterado em qualquer lugar do código,
// essa função é engatilhada automaticamente.
store.subscribe('currentPage', (page) => {
    if (page === "home") {
        root.innerHTML = '<home-page></home-page>';
    } else if (page === "matchmaking") {
        root.innerHTML = '<matchmaking-page></matchmaking-page>';
    } else if (page === "game") {
        root.innerHTML = '<game-page></game-page>';
    }
});

// Inicializa o app na home
store.state.currentPage = 'home';
```
src/services
gameService.js
```javascript
import { store } from "../state/store.js";
import { wsManager } from "../websocket/socket.js";

export const GameService = {
    // Agora aceita poderes, não apenas revelação básica
    playTurn(x, y, powerId = null, powerName = null) {
        if (store.state.currentTurnPlayerId !== store.state.user.id) {
            console.warn("Não é o seu turno!");
            return;
        }

        const actionPayload = powerId ? {
            type: powerName,
            actionId: powerId,
            position: { x, y }
        } : {
            type: "REVEAL",
            position: { x, y }
        };

        wsManager.send({
            type: "PLAYER_ACTION",
            tokenGameId: store.state.tokenGameId,
            action: actionPayload
        });
    },

    discardPower(powerId) {
        wsManager.send({
            type: "DISCARD_POWER",
            tokenGameId: store.state.tokenGameId,
            powerId: powerId
        });
    }
};
```

src/websocket
socket.js
```javascript
import { store } from "../state/store.js";

const wsUrl = "ws://localhost:8080/ws/game?token=";
export let gameWs = null;

export const wsManager = {
    connect(token, userId) {
        gameWs = new WebSocket(`${wsUrl}${token}`);

        gameWs.onopen = () => {
            console.log("🟢 WebSocket Aberto.");
            // Usando o CATACLYSM do seu exemplo para ter inventário
            this.send({ type: "MATCHMAKING_GAME", gameMode: "CATACLYSM" });
        };

        gameWs.onmessage = (event) => {
            const msg = JSON.parse(event.data);
            console.log("📩 Evento Recebido:", msg.event, msg);

            // 1. Partida Encontrada
            if (msg.event === "MATCHMAKING_GAME" && msg.status === "FOUNDED") {
                const opponentInfo = msg.data.players.find(p => p.id !== userId);
                
                store.state.tokenGameId = msg.tokenGameId;
                store.state.opponent = opponentInfo ? 
                    { id: opponentInfo.id, name: opponentInfo.nickname } : 
                    { id: null, name: "Desafiante" };
                
                this.syncGameState(msg.data);

                setTimeout(() => { store.state.currentPage = 'game'; }, 4000);
            }

            // 2. Atualização de Tabuleiro/Jogada
            if (msg.data && msg.data.board) {
                this.syncGameState(msg.data);
            }

            // 3. Mudanças de Turno
            if (msg.event === "TURN_EXPIRED") {
                store.state.currentTurnPlayerId = msg.data.currentTurnPlayerId;
                this.updateTurnMessage();
            }

            // 4. Inatividade ou Fim de Jogo
            if (msg.event === "REMOVED_BECAUSE_INACTIVITY") {
                store.state.gameMessage = "REMOVIDO POR INATIVIDADE!";
            }

            if (msg.event === "GAME_OVER") {
                const isWinner = msg.data.winner.id === userId;
                store.state.gameMessage = isWinner ? "🏆 VOCÊ VENCEU!" : "💀 VOCÊ PERDEU!";
                store.state.tokenGameId = null; // Trava o tabuleiro
            }
        };
    },

    // Centraliza a atualização dos dados da partida
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
    },

    send(payload) {
        if (gameWs && gameWs.readyState === WebSocket.OPEN) {
            gameWs.send(JSON.stringify(payload));
        }
    }
};
```

src/state
store.js
```javascript
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
    words: [], // Novo: Lista de palavras
    players: [], // Novo: Para controlar o inventário de poderes
    gameMessage: 'Aguardando...' // Novo: Feedback visual (ex: Seu turno, Fim de jogo)
});
```
src/styles/
style.css
```css
@import url("https://fonts.googleapis.com/css2?family=Lilita+One&display=swap");

/* === COMPONENTES === */
@import url("./components/board.css");
@import url("./components/inventory.css");
@import url("./components/words.css");
@import url("./components/player.css");

/* === PÁGINAS === */
@import url("./pages/home.css");
@import url("./pages/matchmaking.css");
@import url("./pages/game.css");


/* =========================================
   ESTILOS GLOBAIS E UI BASE
========================================= */
body {
    background-image: url("../../public/background.png");
    background-repeat: no-repeat;
    background-size: cover;
    background-position: center;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
    box-sizing: border-box;
    margin: 0;
    font-family: "Lilita One", cursive;
}

/* Deixamos o Input e o Button no global porque são elementos genéricos de UI 
   que você pode usar na Home, em Modais ou em outras páginas */
.input {
  width: 100%;
  max-width: 100%;
  background-color: white;
  padding: 0.6rem 1rem;
  border-radius: 2rem;
  border: 3px solid #ccc;
  outline: none;
  text-align: center;
  box-sizing: border-box;
  font-size: clamp(1rem, 1.5vw, 1.3rem);
  height: 3rem;
  transition: border-color 0.2s ease;
}

.input:focus {
  border: 3px solid rgba(0, 0, 0, 0.4);
  opacity: 1;
  color: black;
}

.button {
  width: 45%;
  flex: none;
  height: 53px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  border-radius: 2rem;
  padding: 0;
  font-weight: bold;
  font-size: clamp(15px, 6vw, 17px);
  white-space: nowrap;
  color: white;
  cursor: pointer;
  transition: transform 0.1s ease, box-shadow 0.2s ease;
  box-shadow: 0 6px 8px rgba(0, 0, 0, 0.2);
  outline: none;
  background-color: rgb(27, 157, 222);
  border: 5px solid rgb(64, 176, 232);
  box-shadow: 0 6px 0 rgb(57, 68, 148);
  margin-top: 2rem;
}

.button:hover,
.button:focus {
  transform: translateY(-2px);
  border: 5px solid rgb(22, 133, 186);
}

```
src/styles/components
board.css
```css
.board-grid {
    display: grid;
    /* Cria 10 colunas de tamanhos iguais */
    grid-template-columns: repeat(10, 1fr);
    /* Cria 10 linhas */
    grid-template-rows: repeat(10, 1fr);
    gap: 4px; /* Espaço entre as células */
    
    /* Tamanho e centralização na tela */
    width: 450px;
    height: 450px;
    margin: 0 auto;
    background-color: rgba(255, 255, 255); /* Fundo escuro atrás das células */
    padding: 10px;
    border-radius: 8px;
    box-shadow: 0 10px 30px rgba(0,0,0,0.5);
    
    /* Garante que ele fique acima do background de raio */
    position: relative;
    z-index: 10;
}

/* Estilo base para cada célula */
.cell {
    background-color: #ffffff; /* Cor escura padrão */
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

/* Efeito ao passar o mouse apenas em células que não foram reveladas */
.cell.hidden:hover {
    background-color: #34495e;
    transform: scale(1.05);
}

/* Estilo para quando a célula for revelada */
.cell.revealed {
    background-color: #ecf0f1; /* Fundo claro */
    color: #2c3e50; /* Letra escura */
    cursor: default;
    box-shadow: none;
    transform: scale(0.95); /* Dá um leve efeitinho de afundado */
}
```

src/styles/components
inventory.css
```
/* =========================================
   INVENTÁRIO E PODERES (InventoryComponent)
========================================= */
.inventory-section {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 1rem;
    width: 100%;
}

.inventory {
    display: flex;
    gap: 12px;
    justify-content: center;
}

/* O quadrado vazio */
.slot {
    width: 80px;
    height: 80px;
    border: 3px dashed rgba(255, 255, 255, 0.4);
    background-color: rgba(0, 0, 0, 0.3);
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 12px;
    text-align: center;
    text-transform: uppercase;
    color: white;
    transition: all 0.2s ease-in-out;
    user-select: none;
}

/* O quadrado quando tem uma carta/poder */
.slot.has-power {
    border: 3px solid #2ecc71; /* Borda verde */
    background-color: #eafaf1; /* Fundo clarinho */
    font-weight: bold;
    color: #27ae60;
    cursor: pointer;
    box-shadow: 0 4px 6px rgba(0,0,0,0.2);
}

.slot.has-power:hover {
    transform: translateY(-5px); /* Dá um pulinho pra cima */
    box-shadow: 0 6px 12px rgba(0,0,0,0.3);
}

/* O quadrado quando você Clica nele para usar */
.slot.selected {
    background-color: #2e80cc; /* Fundo azul */
    border-color: #1a5282;
    color: white;
    transform: scale(1.1) translateY(-5px);
    box-shadow: 0 0 15px rgba(46, 128, 204, 0.8);
}

/* Botão de Descartar */
.discard.button {
    background-color: #e74c3c; /* Vermelho */
    border: 5px solid #c0392b;
    box-shadow: 0 6px 0 #922b21;
    width: auto;
    padding: 0 2rem;
    height: 45px;
    margin-top: 0;
}

.discard.button:hover,
.discard.button:focus {
    border-color: #a93226;
    background-color: #cd6155;
}
```

src/styles/components
player.css
```
/* =========================================
   COMPONENTES: PLAYER CARD
========================================= */

/* O Web Component atua como um container base */
player-card {
    display: block;
    width: 250px;
    transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

.player-card {
    /* Variáveis locais que serão alteradas dinamicamente via JS */
    --card-color: #7f8c8d; /* Cor neutra padrão */
    --glow-color: transparent;
    
    display: flex;
    align-items: center;
    background-color: rgba(30, 30, 30, 0.9);
    border: 3px solid var(--card-color);
    border-radius: 16px;
    padding: 12px;
    gap: 15px;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.3), 0 0 0 var(--glow-color);
    
    /* A mágica da animação suave (Lerp de cor, sombra e posição) */
    transition: border-color 0.4s ease, box-shadow 0.4s ease, transform 0.4s ease, opacity 0.4s ease;
}

/* =========================================
   ESTADOS DE TURNO (UX)
========================================= */

/* Quando for a vez deste jogador */
.player-card.active {
    opacity: 1;
    transform: translateX(10px) scale(1.05); /* Slide + Pop */
    --glow-color: var(--card-color);
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.5), 0 0 20px var(--glow-color);
}

/* Quando NÃO for a vez deste jogador */
.player-card.inactive {
    opacity: 0.5;
    transform: translateX(-5px) scale(0.95); /* Slide reverso + Diminui */
    filter: grayscale(30%);
}

/* =========================================
   CONTEÚDO INTERNO
========================================= */

.player-card__avatar {
    width: 60px;
    height: 60px;
    border-radius: 50%;
    border: 2px solid var(--card-color);
    background-color: #2c3e50;
    object-fit: cover;
    transition: border-color 0.4s ease;
}

.player-card__info {
    display: flex;
    flex-direction: column;
    flex-grow: 1;
}

.player-card__turn-text {
    font-size: 12px;
    font-weight: bold;
    text-transform: uppercase;
    color: var(--card-color);
    margin-bottom: 4px;
    transition: color 0.4s ease;
}

.player-card__name {
    color: white;
    font-size: 18px;
    margin: 0 0 8px 0;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

/* =========================================
   SISTEMA DE PODER (BOLINHAS)
========================================= */

.power-dots {
    display: flex;
    gap: 6px;
}

.dot {
    width: 12px;
    height: 12px;
    border-radius: 50%;
    border: 2px solid var(--card-color);
    background-color: transparent;
    transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275); /* Efeito Pop */
}

/* Bolinha preenchida (com poder) */
.dot.filled {
    background-color: white;
    border-color: white;
    transform: scale(1.1);
    box-shadow: 0 0 5px white;
}
```

src/styles/components
words.css
```
/* =========================================
   PALAVRAS (WordsComponent)
========================================= */
.words-container {
    width: 100%;
    display: flex;
    justify-content: center;
}

.words {
    display: flex;
    flex-wrap: wrap; /* Se a tela for pequena, as palavras descem de linha */
    justify-content: center;
    align-items: center;
    gap: 1.5rem;
    padding: 1rem;
    background-color: rgba(255, 255, 255, 0.1); /* Fundo levemente translúcido */
    border-radius: 12px;
    box-shadow: inset 0 4px 6px rgba(0,0,0,0.1);
}

.word {
    padding: 0.5rem 1rem;
    margin: 0;
    font-size: 1.8rem;
    text-transform: uppercase;
    color: white;
    font-weight: bold;
    letter-spacing: 2px;
    -webkit-text-stroke: 1px #464646; /* Borda preta na letra */
    transition: all 0.3s ease;
}

/* O estilo inline no JS vai adicionar o line-through, mas o CSS aqui 
   garante que a transição de opacidade fique suave */
.word[style*="line-through"] {
    color: #a0a0a0;
    -webkit-text-stroke: 0;
    transform: scale(0.95);
}
```

src/styles/pages
game.css
```
game-page {
    display: flex;
    width: 100vw;
    height: 100vh;
    align-items: center;
    justify-content: center;
    position: absolute;
    top: 0;
    left: 0;
}

.game-layout {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 1.5rem;
    width: 100%;
    max-width: 800px;
    margin: 0 auto;
    padding: 1rem;
    box-sizing: border-box;
    position: relative;
    z-index: 10;
}
```

src/styles/pages
matchmaking.css
```
.matchmaking-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100vh;
    color: white;
    -webkit-text-stroke: 1.5px #464646;
    text-align: center;
}

.spinner {
    width: 50px;
    height: 50px;
    border: 15px solid rgba(255, 255, 255, 0.3);
    border-radius: 50%;
    border-top-color: #f17f15;
    animation: spin 1s ease-in-out infinite;
    margin-top: 20px;
}

@keyframes spin {
    to { transform: rotate(360deg); }
}

.matchmaking-hide {
    display: none !important;
}

.vs-text {
    font-size: 24px;
    color: #eeb807;
    margin: 15px 0;
}
```