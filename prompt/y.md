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
    } 
});

--------------------
import { store } from "../state/store.js";

const wsUrl = "ws://localhost:8080/ws/game?token=";
export let gameWs = null;

export const wsManager = {
    connect(token, userId) {
        gameWs = new WebSocket(`${wsUrl}${token}`);

        gameWs.onopen = () => {
            console.log("🟢 WebSocket Aberto.");
            this.send({ type: "MATCHMAKING_GAME", gameMode: "CATACLYSM" });
        };

        gameWs.onmessage = (event) => {
            const msg = JSON.parse(event.data);
            console.log("📩 Evento Recebido:", msg.event, msg);

            if (msg.event === "MATCHMAKING_GAME" && msg.status === "FOUNDED") {
                const opponentInfo = msg.data.players.find(p => p.id !== userId);
                
                store.state.tokenGameId = msg.tokenGameId;
                store.state.opponent = opponentInfo ? 
                    { id: opponentInfo.id, name: opponentInfo.nickname } : 
                    { id: null, name: "Desafiante" };
                
                this.syncGameState(msg.data);

                store.state.currentPage = 'found';
            }

            if (msg.data && msg.data.board) {
                this.syncGameState(msg.data);
            }

            if (msg.event === "TURN_EXPIRED") {
                store.state.currentTurnPlayerId = msg.data.currentTurnPlayerId;
                this.updateTurnMessage();
            }

            if (msg.event === "REMOVED_BECAUSE_INACTIVITY") {
                const removedId = msg.data ? msg.data.participantId : null;
                const isMe = removedId === store.state.user.id;

        // Feedback visual simples usando alert (ou seu modal, se preferir)
            if (isMe) {
                alert("Você perdeu por inatividade (tempo esgotado).");
            } else {
                alert("Você venceu! O oponente foi desconectado por inatividade.");
            }

        // QUEBRA O SOFT LOCK: Bloqueia a partida e manda para a home
                store.state.tokenGameId = null; 
                store.state.currentPage = 'home';
                return; // Encerra a execução deste evento
            }

    // CORREÇÃO 2: Tratar o Abandono (Botão Sair ou Aba Fechada)
            if (msg.event === "PARTICIPANT_LEAVE" || msg.event === "PARTICIPANT_DISCONNECTED") {
                const leftId = msg.data ? msg.data.participantId : null;

        // Se quem saiu não foi você, significa que o oponente fugiu = Vitória sua
            if (leftId && leftId !== store.state.user.id) {
                alert("Você venceu! O oponente abandonou a partida.");
            
            // QUEBRA O SOFT LOCK
                store.state.tokenGameId = null;
                store.state.currentPage = 'home';
                return;
                }
            }

            if (msg.event === "GAME_OVER") {
                const isWinner = msg.data.winner.id === store.state.user.id;
                alert(isWinner ? "🏆 VOCÊ VENCEU!" : "💀 VOCÊ PERDEU!");
        
                store.state.tokenGameId = null; // Trava o tabuleiro
                store.state.currentPage = 'home'; // Redireciona
            }

            if (msg.event === "ERROR") {
                if (msg.message === "player_not_in_game") {
                    alert("Você foi removido da partida por inatividade ou a sala foi fechada.");
                    
                    // Limpa o estado e manda para a Home para quebrar o travamento
                    store.state.tokenGameId = null;
                    store.state.currentPage = 'home';
                    return;
                } else {
                    console.error("Erro inesperado da API:", msg.message);
                }
                }
        };
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
    },

    send(payload) {
        if (gameWs && gameWs.readyState === WebSocket.OPEN) {
            gameWs.send(JSON.stringify(payload));
        }
    },

    scheduleHomeRedirect() {
        setTimeout(() => {
            store.state.endGameState = { show: false, title: '', message: '', type: '' };
            store.state.currentPage = 'home';
        }, 4000); 
    }
};

--------------------

import { store } from "../state/store.js";
import { wsManager } from "../websocket/socket.js";

export const GameService = {
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
    },

    leaveGame(isClosingTab = false){
        if (!store.state.tokenGameId) return;

        wsManager.send({
            type: "LEFT_GAME",
            tokenGameId: store.state.tokenGameId
        });

        store.state.tokenGameId = null; 
        
        if(!isClosingTab) {
            store.state.currentPage = 'home';
        }
    }
};
window.addEventListener('beforeunload', () => {
    if (store.state.tokenGameId) {
        GameService.leaveGame(true);
    }
});

--------------------

import { GameService } from "../services/gameService.js";
import { store } from "../state/store.js";
import "../components/board.js";
import "../components/player.js";
import "../components/words.js";
import "../components/inventory.js";
import "../components/modal.js";

export class GamePage extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
            <div class="game-layout">
            <div style="width: 100%; display: flex; justify-content: flex-end;">
                    <button id="btn-leave" class="discard button" style="height: 35px; font-size: 14px; margin: 0;">Sair da Partida</button>
                </div>
                <player-card></player-card>
                <words-component></words-component>
                <board-component></board-component>
                <inventory-component></inventory-component>
                
                <end-modal></end-modal>
            </div>
        `;

        this.querySelector('#btn-leave').addEventListener('click', () => {
            // Uma pequena confirmação nativa evita cliques acidentais
            if (confirm("Tem certeza qyue deseja abandonar a partida? Você receberá uma derrota.")) {
                GameService.leaveGame();
            }
        });

        this.addEventListener('cell-clicked', (e) => {
            const { x, y } = e.detail;
            const power = store.state.selectedPower; 
            
            if (power) {
                GameService.playTurn(parseInt(x), parseInt(y), power.id, power.name);
                store.state.selectedPower = null;
            } else {
                GameService.playTurn(parseInt(x), parseInt(y));
            }
        });

        this.addEventListener('discard-power', (e) => {
            const { powerId } = e.detail;
            GameService.discardPower(powerId);
            store.state.selectedPower = null;
        });
    }
}
customElements.define("game-page", GamePage);

--------------------

import { store } from "../state/store.js";

export class PlayerCard extends HTMLElement {
    constructor() {
        super();
        this.currentlyShowingId = null;
        this.isAnimating = false;
    }

    connectedCallback() {
        this.innerHTML = `
            <div class="active-card-container">
                <div class="player-card" id="dynamic-card">
                    <img class="player-card__avatar" id="dyn-avatar" src="../../assets/avatar-3.png" alt="Avatar">
                    
                    <div class="player-card__info">
                        <h3 class="player-card__name" id="dyn-name">...</h3>
                        
                        <div class="power-dots" id="dyn-powers"></div>s
                    </div>
                </div>
            </div>
        `;

        this.cardEl = this.querySelector('#dynamic-card');
        this.labelEl = this.querySelector('#dyn-label');
        this.nameEl = this.querySelector('#dyn-name');
        this.powersEl = this.querySelector('#dyn-powers');

        store.subscribe('currentTurnPlayerId', (newTurnId) => this.handleTurnChange(newTurnId));
        
        if (store.state.currentTurnPlayerId) {
            this.handleTurnChange(store.state.currentTurnPlayerId);
        }
    }

    handleTurnChange(newTurnId) {
        if (!newTurnId || this.currentlyShowingId === newTurnId || this.isAnimating) return;

        this.isAnimating = true;

        if (!this.currentlyShowingId) {
            this.updateDOM(newTurnId);
            this.cardEl.classList.add('anim-enter');
            this.finalizeAnimation(newTurnId);
            return;
        }

        this.cardEl.classList.remove('anim-enter');
        this.cardEl.classList.add('anim-exit');

        setTimeout(() => {
            this.updateDOM(newTurnId);
            
            this.cardEl.classList.remove('anim-exit');
            this.cardEl.classList.add('anim-enter');
            
            this.finalizeAnimation(newTurnId);
        }, 300);
    }

    updateDOM(playerId) {
        const isMe = playerId === store.state.user.id;
        
        const name = isMe ? store.state.user.name : store.state.opponent.name;
        // const labelText = isMe ? 'Sua vez' : 'Vez do oponente';

        this.cardEl.classList.remove('theme-orange', 'theme-blue');
        if (isMe) {
            this.cardEl.classList.add('theme-orange');
        } else {
            this.cardEl.classList.add('theme-blue');
        }
        
        this.nameEl.innerText = name;
        // this.labelEl.innerText = labelText;

        const playersList = store.state.players || [];
        const playerData = playersList.find(p => p.id === playerId);
        
        let powerHtml = '';
        if (playerData && playerData.inventory) {
            const powerCount = playerData.inventory.filter(item => item !== null).length;
            for (let i = 0; i < 5; i++) {
                const filledClass = i < powerCount ? 'filled' : 'empty';
                powerHtml += `<span class="dot ${filledClass}"></span>`;
            }
        }
        this.powersEl.innerHTML = powerHtml;
    }

    finalizeAnimation(newId) {
        setTimeout(() => {
            this.isAnimating = false;
            this.currentlyShowingId = newId;
        }, 300);
    }
}

--------------------



--------------------