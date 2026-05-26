class User {
    constructor(id, nickname, token) {
        this.id = id;
        this.nickname = nickname;
        this.token = token;
    }
}

const httpUrl = "http://localhost:8080";
const wsUrl = "ws://localhost:8080";

const startButton = document.getElementById("start");
const nicknameInput = document.getElementById("nickname");
const turnDisplay = document.getElementById("turn");
const wordsContainer = document.getElementById("words");
const discardButton = document.getElementById("discard");
discardButton.classList.add("hide");

discardButton.addEventListener("click", () => discardPower());

let gameWs = null;
let currentUser = null;
let currentTokenGameId = null;
let opponentId = null;
let selectedPower = null;

function clearPowerSelection() {
    selectedPower = null;
    document.querySelectorAll(".slot").forEach(s => s.classList.remove("selected"));
    discardButton.classList.add("hide");
}

function updateInventory(players, id) {
    const me = players.find(p => p.id === id);
    const opponent = players.find(p => p.id !== id);
    
    if (opponent) opponentId = opponent.id;
    if (!me) return;

    for (let i = 1; i <= 5; i++) {
        const slot = document.getElementById(`slot${i}`);
        slot.textContent = ""; 
        slot.classList.remove("has-power");
        slot.dataset.powerId = "";
        slot.dataset.powerName = "";
    }

    me.inventory.forEach((power, index) => {
        const slotIndex = index + 1;
        const slot = document.getElementById(`slot${slotIndex}`);
        
        if (slot) {
            slot.textContent = power.name;
            slot.classList.add("has-power");
            slot.dataset.powerId = power.id;
            slot.dataset.powerName = power.name;

            if (selectedPower && selectedPower.id === power.id) {
                slot.classList.add("selected");
                discardButton.classList.remove("hide");
            }
        }
    });
}

async function registerAndLogin(nickname, email, password) {
    await fetch(`${httpUrl}/user`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ nickname, email, password })
    }).catch(() => {});

    const login = await fetch(`${httpUrl}/user/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password })
    }).then(res => res.json());

    return new User(login.data.id, nickname, login.data.token);
}

function discardPower() {
    gameWs.send(JSON.stringify({ type: "DISCARD_POWER", tokenGameId: currentTokenGameId,  powerId: selectedPower.id }));
}

function updateBoard(board) {
    board.forEach((row, x) => {
        row.forEach((cell, y) => {
            const cellDiv = document.getElementById(`${x}-${y}`);

            if (cellDiv) {
                cellDiv.innerText = cell.letter || "";
                cellDiv.className = "cell";

                if (!cell.revealed && !cell.letter && !cell.revealedBy && !cell.effect) {
                    cellDiv.style.backgroundColor = "#f5f5f5f5";
                }

                if (cell.revealed) {
                    cellDiv.classList.add("revealed");
                    cellDiv.style.backgroundColor = cell.revealedBy === currentUser.id ? "#1587f1ff" : "#eeb807ff";
                }

                if (cell.effect) {
                    const areBlock = cell.effect.effect === "BLOCK";

                    cellDiv.classList.add(areBlock ? "blocked" : "trapped");
                    cellDiv.style.backgroundColor = areBlock ? cell.effect.ownerId === currentUser.id ? "#164b7cff" : "#977916ff" : cell.effect.ownerId === currentUser.id ? "#0400ffff" : "#ff6600f5";
                    cellDiv.innerText = cell.effect.remainingClicks || "";
                }

                if (cell.revealed && !cell.letter && !cell.revealedBy) {
                    cellDiv.style.backgroundColor = "#1a1a1af5";
                }
            }
        });
    });
}

function updateWords(words) {
    wordsContainer.innerHTML = "";
    words.forEach(w => {
        const p = document.createElement("p");
        p.className = "word";
        if (w.found) p.style.textDecoration = "line-through";
        p.innerText = w.word;
        wordsContainer.appendChild(p);
    });
}

function sendAction(type, payload) {
    if (gameWs && gameWs.readyState === WebSocket.OPEN) {
        gameWs.send(JSON.stringify({ type, ...payload }));
    }
}

document.querySelector(".grid").addEventListener("click", (e) => {
    if (e.target.classList.contains("cell") && currentTokenGameId) {
        const [x, y] = e.target.id.split("-").map(Number);
        
        if (selectedPower) {
            sendAction("PLAYER_ACTION", {
                tokenGameId: currentTokenGameId,
                action: {
                    type: selectedPower.name,
                    actionId: selectedPower.id,
                    position: { x, y }
                }
            });
            clearPowerSelection();
        } else {
            sendAction("PLAYER_ACTION", {
                tokenGameId: currentTokenGameId,
                action: {
                    type: "REVEAL",
                    position: { x, y }
                }
            });
        }
    }
});

document.querySelectorAll(".slot").forEach((slot, index) => {
    slot.addEventListener("click", () => {
        const powerId = slot.dataset.powerId;
        const powerName = slot.dataset.powerName;

        if (!powerId) return;

        if (selectedPower && selectedPower.id === powerId) {
            sendAction("PLAYER_ACTION", {
                tokenGameId: currentTokenGameId,
                action: {
                    type: powerName,
                    actionId: powerId,
                    targetId: opponentId
                }
            });
            clearPowerSelection();
        } else {
            clearPowerSelection();
            selectedPower = { id: powerId, name: powerName, slotIndex: index + 1 };
            slot.classList.add("selected");
            discardButton.classList.remove("hide");
        }
    });
});

startButton.addEventListener("click", async () => {
    const nick = nicknameInput.value;
    if (!nick) return alert("Digite um nickname");

    startButton.style.display = "none";
    nicknameInput.style.display = "none";
    
    currentUser = await registerAndLogin(nick, `${nick}@email.com`, "12345678");
    gameWs = new WebSocket(`${wsUrl}/ws/game?token=${currentUser.token}`);

    gameWs.onopen = () => {
        console.log("Conectado!");
        sendAction("MATCHMAKING_GAME", { gameMode: "CATACLYSM" });
        turnDisplay.innerText = "Buscando partida...";
    };

    gameWs.onmessage = (event) => {
        const msg = JSON.parse(event.data);
        console.log("Evento DEBUG:", msg);

        if (msg.event === "REMOVED_BECAUSE_INACTIVITY") {
            turnDisplay.innerText = "REMOVIDO POR INATIVIDADE!";
            turnDisplay.style.color = "gray";
        }

        if (msg.event === "MATCHMAKING_GAME" && msg.status === "FOUNDED") {
            currentTokenGameId = msg.tokenGameId;
        }

        if (msg.event === "POWER_DISCARDED") {
            updateInventory(msg.data.players, nick);
            clearPowerSelection();
        }

        if (msg.event === "TURN_EXPIRED") {
            const isMyTurn = msg.data.currentTurnPlayerId === currentUser.id;
            turnDisplay.innerText = isMyTurn ? "SEU TURNO" : "Turno do oponente";
            turnDisplay.style.color = isMyTurn ? "green" : "red";
        }

        if (msg.data && msg.data.board) {
            updateBoard(msg.data.board);
            updateWords(msg.data.words);
            updateInventory(msg.data.players, currentUser.id);
            
            const isMyTurn = msg.data.currentTurnPlayerId === currentUser.id;
            turnDisplay.innerText = isMyTurn ? "SEU TURNO" : "Turno do oponente";
            turnDisplay.style.color = isMyTurn ? "green" : "red";
        }

        if (msg.event === "GAME_OVER") {
            const winner = msg.data.winner.id === currentUser.id ? "VOCÊ VENCEU!" : "VOCÊ PERDEU!";
            turnDisplay.innerText = `FIM DE JOGO: ${winner}`;
            turnDisplay.style.color = msg.data.winner.id === currentUser.id ? "green" : "red";
            currentTokenGameId = null;
        }
    };
});