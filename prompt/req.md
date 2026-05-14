
```
import WebSocket from "ws";

const endpoint = "http://localhost:8080";
const wspoint = "ws://localhost:8080/ws/game";

class User {
  constructor(nickname, email, password) {
    this.nickname = nickname;
    this.email = email;
    this.password = password;
  }

  setAuth(data) {
    this.id = data.id;
    this.token = data.token;
  }
}

const users = [
  new User("Zidan", "zidan@email.com", "12345678"),
  new User("Ronaldo", "ronaldo@email.com", "12345678")
];

const events = [];

/* =========================
   HTTP HELPERS
========================= */

async function http(method, path, body) {
  const res = await fetch(`${endpoint}${path}`, {
    method,
    headers: { "Content-Type": "application/json" },
    body: body ? JSON.stringify(body) : undefined
  }).then(res => res.json());

  console.log(res);

  return res;
}

/* =========================
   AUTH FLOW
========================= */

async function registerAndLogin(user) {
  console.log(`\n👤 Criando usuário: ${user.nickname}`);

  await http("POST", "/user", {
    nickname: user.nickname,
    email: user.email,
    password: user.password
  });

  console.log(`🔐 Logando: ${user.nickname}`);

  const login = await http("POST", "/user/login", {
    email: user.email,
    password: user.password
  });

  user.setAuth(login.data);
}

/* =========================
   WEBSOCKET
========================= */

function connect(user) {
  return new Promise((resolve) => {
    const ws = new WebSocket(`${wspoint}?token=${user.token}`);

    ws.on("open", () => {
      console.log(`🟢 ${user.nickname} conectado`);
      resolve(ws);
    });

    ws.on("message", (data) => {
      const msg = JSON.parse(data);

      console.log(`📩 [${user.nickname}]`, msg);

      if (msg.data && msg.data.board) {
        const board = msg.data.board;

        const lettersBoard = board.map(row =>
          row.map(cell => cell.letter)
        );

        console.table(lettersBoard);
      }

      events.push({ ...msg, user: user.nickname });
    });

    ws.on("close", () => {
      console.log(`🔴 ${user.nickname} desconectado`);
    });
  });
}

function send(ws, payload) {
  ws.send(JSON.stringify(payload));
}

/* =========================
   EVENT WAIT
========================= */

function waitForEvent(predicate, timeout = 5000) {
  return new Promise((resolve, reject) => {
    const start = Date.now();

    const interval = setInterval(() => {
      const index = events.findIndex(predicate);

      if (index !== -1) {
        const event = events.splice(index, 1)[0];
        clearInterval(interval);
        resolve(event);
      }

      if (Date.now() - start > timeout) {
        clearInterval(interval);
        reject("Timeout esperando evento");
      }
    }, 10);
  });
}

/* =========================
   GAME FLOW
========================= */

async function runGameFlow(ws1, ws2) {
  console.log("\n🎮 Buscando jogo...");

  send(ws1, {
    type: "MATCHMAKING_GAME",
    gameMode: "NORMAL"
  });

  send(ws2, {
    type: "MATCHMAKING_GAME",
    gameMode: "NORMAL"
  });

  const started = await waitForEvent(e => (e.event === "MATCHMAKING_GAME" && e.status === "FOUNDED"));
  const tokenGameId = started.tokenGameId;

  let currentPlayer = started.data.currentTurnPlayerId;

  const positions = [];
  for (let x = 0; x < 10; x++) {
    for (let y = 0; y < 10; y++) {
      positions.push({ x, y });
    }
  }

  let gameRunning = true;

  while (gameRunning) {
    if (positions.length === 0) {
      console.log("⚠️ Sem mais posições disponíveis");
      break;
    }

    const pos = positions.splice(Math.floor(Math.random() * positions.length), 1)[0];

    const currentWs =
      currentPlayer === users[0].id ? ws1 : ws2;

    send(currentWs, {
      type: "PLAYER_ACTION",
      tokenGameId,
      action: {
        type: "REVEAL",
        position: pos
      }
    });

    const result = await waitForEvent(
      e => e.event === "GAME_OVER" ||
      (
        e.event === "PLAYER_ACTION_RESULT" &&
        e.data.currentTurnPlayerId !== currentPlayer
      ) 
    );

    if (result.event === "GAME_OVER") {
      console.log("🏁 Fim do jogo!");
      gameRunning = false;
      break;
    }

    currentPlayer = result.data.currentTurnPlayerId;

    await sleep(100);
  }
}

async function main() {
  console.log("🚀 Iniciando testes...");

  // auth
  for (const user of users) {
    await registerAndLogin(user);
  }

  // buscar user (testa GET)
  await http("GET", `/user/${users[0].id}`);

  // sockets
  const ws1 = await connect(users[0]);
  const ws2 = await connect(users[1]);

  await runGameFlow(ws1, ws2);
}

function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

main().catch(console.error);
```


```
import WebSocket from "ws";

const endpoint = "http://localhost:8080";
const wspoint = "ws://localhost:8080/ws/game";

class User {
  constructor(nickname, email, password) {
    this.nickname = nickname;
    this.email = email;
    this.password = password;
  }

  setAuth(data) {
    this.id = data.id;
    this.token = data.token;
  }
}

const users = [
  new User("Zidan", "zidan@email.com", "12345678"),
  new User("Ronaldo", "ronaldo@email.com", "12345678"),
  new User("Rogerio", "rogerio@email.com", "12345678"),
];

const events = [];

/* =========================
   HTTP HELPERS
========================= */

async function http(method, path, body) {
  const res = await fetch(`${endpoint}${path}`, {
    method,
    headers: { "Content-Type": "application/json" },
    body: body ? JSON.stringify(body) : undefined
  }).then(res => res.json());

  console.log(res);

  return res;
}

/* =========================
   AUTH FLOW
========================= */

async function registerAndLogin(user) {
  console.log(`\n👤 Criando usuário: ${user.nickname}`);

  await http("POST", "/user", {
    nickname: user.nickname,
    email: user.email,
    password: user.password
  });

  console.log(`🔐 Logando: ${user.nickname}`);

  const login = await http("POST", "/user/login", {
    email: user.email,
    password: user.password
  });

  user.setAuth(login.data);
}

/* =========================
   WEBSOCKET
========================= */

function connect(user) {
  return new Promise((resolve) => {
    const ws = new WebSocket(`${wspoint}?token=${user.token}`);

    ws.on("open", () => {
      console.log(`🟢 ${user.nickname} conectado`);
      resolve(ws);
    });

    ws.on("message", (data) => {
      const msg = JSON.parse(data);

      console.log(`📩 [${user.nickname}]`, msg);

      if (msg.data && msg.data.board) {
        const board = msg.data.board;

        const lettersBoard = board.map(row =>
          row.map(cell => cell.letter)
        );

        console.table(lettersBoard);
      }

      events.push({ ...msg, user: user.nickname });
    });

    ws.on("close", () => {
      console.log(`🔴 ${user.nickname} desconectado`);
    });
  });
}

function send(ws, payload) {
  ws.send(JSON.stringify(payload));
}

/* =========================
   EVENT WAIT
========================= */

function waitForEvent(predicate, timeout = 5000) {
  return new Promise((resolve, reject) => {
    const start = Date.now();

    const interval = setInterval(() => {
      const index = events.findIndex(predicate);

      if (index !== -1) {
        const event = events.splice(index, 1)[0];
        clearInterval(interval);
        resolve(event);
      }

      if (Date.now() - start > timeout) {
        clearInterval(interval);
        reject("Timeout esperando evento");
      }
    }, 10);
  });
}

/* =========================
   GAME FLOW
========================= */

async function runGameFlow(ws1, ws2, ws3) {
  console.log("\n🎮 Criando jogo...");

  send(ws1, {
    type: "CREATE_GAME",
    name: "Sala Teste",
    settings: {
      allowSpectators: true,
      privateGame: false
    }
  });

  const created = await waitForEvent(e => e.event === "GAME_CREATED");

  // pegar token via HTTP (testa endpoint)
  const games = await http("GET", "/game");

  const tokenGameId = games.data.games[0]?.tokenGameId;

  console.log("🎯 Token do jogo:", tokenGameId);

  /* =========================
     JOIN
  ========================= */

  send(ws2, {
    type: "JOIN_GAME",
    tokenGameId
  });

  await waitForEvent(e => e.event === "PARTICIPANT_JOIN");

  send(ws3, {
    type: "JOIN_GAME",
    tokenGameId
  });

  await waitForEvent(e => e.event === "PARTICIPANT_JOIN");

  /* =========================
     SWAP POSITION
  ========================= */

  send(ws2, {
    type: "SWAP_POSITION",
    tokenGameId,
    position: 3
  });

  await waitForEvent(e => e.event === "POSITIONS_UPDATED");

  send(ws3, {
    type: "SWAP_POSITION",
    tokenGameId,
    position: 1
  });

  await waitForEvent(e => e.event === "POSITIONS_UPDATED");

  /* =========================
     START GAME
  ========================= */

  send(ws1, {
    type: "START_GAME",
    tokenGameId,
    settings: {
      themeId: "tech",
      gameMode: "NORMAL"
    }
  });

  const started = await waitForEvent(e => e.event === "GAME_STARTED");

  let currentPlayer = started.data.currentTurnPlayerId;

  /* =========================
     GAME LOOP
  ========================= */

  const positions = [];
  for (let x = 0; x < 10; x++) {
    for (let y = 0; y < 10; y++) {
      positions.push({ x, y });
    }
  }

  let passTurn = true;

  while (positions.length > 0) {
    const pos = positions.splice(Math.floor(Math.random() * positions.length), 1)[0];

    const currentWs =
      currentPlayer === users[0].id ? ws1 :
      currentPlayer === users[1].id ? ws2 : ws3;

    send(currentWs, {
      type: "PLAYER_ACTION",
      tokenGameId,
      action: {
        type: "REVEAL",
        position: pos
      }
    });

    const result = await waitForEvent(
      e => e.event === "GAME_OVER" ||
      e.event === "PLAYER_ACTION_RESULT" &&
      e.data.currentTurnPlayerId !== currentPlayer
    );

    if (passTurn) {
      await sleep(47000);

      const result = await waitForEvent(
        e => e.event === "TURN_EXPIRED"
      );

      currentPlayer = result.data.currentTurnPlayerId;
      passTurn = false;

      await sleep(3000);

      continue;
    }

    if (result.event === "GAME_OVER") {
      console.log("🏁 Fim do jogo!");
      break;
    }

    currentPlayer = result.data.currentTurnPlayerId;
  }

  /* =========================
     KICK / BAN / UNBAN TEST
  ========================= */

  console.log("\n⚖️ Testando moderação...");

  send(ws1, {
    type: "KICK_PARTICIPANT",
    tokenGameId,
    participantId: users[2].id
  });

  await waitForEvent(e => e.event === "PARTICIPANT_KICKED");

  send(ws1, {
    type: "BAN_PARTICIPANT",
    tokenGameId,
    participantId: users[1].id
  });

  await waitForEvent(e => e.event === "PARTICIPANT_BANNED");

  send(ws1, {
    type: "UNBAN_PARTICIPANT",
    tokenGameId,
    userId: users[1].id
  });

  await waitForEvent(e => e.event === "PARTICIPANT_UNBANNED");

  send(ws2, {
    type: "JOIN_GAME",
    tokenGameId
  });

  await waitForEvent(e => e.event === "PARTICIPANT_JOIN");

  /* =========================
     LEAVE
  ========================= */

  send(ws2, {
    type: "LEFT_GAME",
    tokenGameId
  });

  await waitForEvent(e => e.event === "PARTICIPANT_LEAVE");

  console.log("\n✅ Teste finalizado com sucesso");
}

/* =========================
   MAIN
========================= */

async function main() {
  console.log("🚀 Iniciando testes...");

  // auth
  for (const user of users) {
    await registerAndLogin(user);
  }

  // buscar user (testa GET)
  await http("GET", `/user/${users[0].id}`);

  // sockets
  const ws1 = await connect(users[0]);
  const ws2 = await connect(users[1]);
  const ws3 = await connect(users[2]);

  await runGameFlow(ws1, ws2, ws3);
}

function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

main().catch(console.error);
```

---Html, css e js
```
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>UI Test</title>
    <link rel="stylesheet" href="style.css">
    <script src="script.js" defer></script>
</head>
<body class="body">
    <header class="header">
        <h1 class="title">Teste da API</h1>
    </header>

    <main class="main">
        <p class="turn" id="turn"></p>

        <div class="words" id="words">
            <p class="word" id="word1">palavra</p>
            <p class="word" id="word2">palavra</p>
            <p class="word" id="word3">palavra</p>
            <p class="word" id="word4">palavra</p>
            <p class="word" id="word5">palavra</p>
        </div>

        <div class="grid">
            <div class="cell" id="0-0"></div>
            <div class="cell" id="0-1"></div>
            <div class="cell" id="0-2"></div>
            <div class="cell" id="0-3"></div>
            <div class="cell" id="0-4"></div>
            <div class="cell" id="0-5"></div>
            <div class="cell" id="0-6"></div>
            <div class="cell" id="0-7"></div>
            <div class="cell" id="0-8"></div>
            <div class="cell" id="0-9"></div>
            <div class="cell" id="1-0"></div>
            <div class="cell" id="1-1"></div>
            <div class="cell" id="1-2"></div>
            <div class="cell" id="1-3"></div>
            <div class="cell" id="1-4"></div>
            <div class="cell" id="1-5"></div>
            <div class="cell" id="1-6"></div>
            <div class="cell" id="1-7"></div>
            <div class="cell" id="1-8"></div>
            <div class="cell" id="1-9"></div>
            <div class="cell" id="2-0"></div>
            <div class="cell" id="2-1"></div>
            <div class="cell" id="2-2"></div>
            <div class="cell" id="2-3"></div>
            <div class="cell" id="2-4"></div>
            <div class="cell" id="2-5"></div>
            <div class="cell" id="2-6"></div>
            <div class="cell" id="2-7"></div>
            <div class="cell" id="2-8"></div>
            <div class="cell" id="2-9"></div>
            <div class="cell" id="3-0"></div>
            <div class="cell" id="3-1"></div>
            <div class="cell" id="3-2"></div>
            <div class="cell" id="3-3"></div>
            <div class="cell" id="3-4"></div>
            <div class="cell" id="3-5"></div>
            <div class="cell" id="3-6"></div>
            <div class="cell" id="3-7"></div>
            <div class="cell" id="3-8"></div>
            <div class="cell" id="3-9"></div>
            <div class="cell" id="4-0"></div>
            <div class="cell" id="4-1"></div>
            <div class="cell" id="4-2"></div>
            <div class="cell" id="4-3"></div>
            <div class="cell" id="4-4"></div>
            <div class="cell" id="4-5"></div>
            <div class="cell" id="4-6"></div>
            <div class="cell" id="4-7"></div>
            <div class="cell" id="4-8"></div>
            <div class="cell" id="4-9"></div>
            <div class="cell" id="5-0"></div>
            <div class="cell" id="5-1"></div>
            <div class="cell" id="5-2"></div>
            <div class="cell" id="5-3"></div>
            <div class="cell" id="5-4"></div>
            <div class="cell" id="5-5"></div>
            <div class="cell" id="5-6"></div>
            <div class="cell" id="5-7"></div>
            <div class="cell" id="5-8"></div>
            <div class="cell" id="5-9"></div>
            <div class="cell" id="6-0"></div>
            <div class="cell" id="6-1"></div>
            <div class="cell" id="6-2"></div>
            <div class="cell" id="6-3"></div>
            <div class="cell" id="6-4"></div>
            <div class="cell" id="6-5"></div>
            <div class="cell" id="6-6"></div>
            <div class="cell" id="6-7"></div>
            <div class="cell" id="6-8"></div>
            <div class="cell" id="6-9"></div>
            <div class="cell" id="7-0"></div>
            <div class="cell" id="7-1"></div>
            <div class="cell" id="7-2"></div>
            <div class="cell" id="7-3"></div>
            <div class="cell" id="7-4"></div>
            <div class="cell" id="7-5"></div>
            <div class="cell" id="7-6"></div>
            <div class="cell" id="7-7"></div>
            <div class="cell" id="7-8"></div>
            <div class="cell" id="7-9"></div>
            <div class="cell" id="8-0"></div>
            <div class="cell" id="8-1"></div>
            <div class="cell" id="8-2"></div>
            <div class="cell" id="8-3"></div>
            <div class="cell" id="8-4"></div>
            <div class="cell" id="8-5"></div>
            <div class="cell" id="8-6"></div>
            <div class="cell" id="8-7"></div>
            <div class="cell" id="8-8"></div>
            <div class="cell" id="8-9"></div>
            <div class="cell" id="9-0"></div>
            <div class="cell" id="9-1"></div>
            <div class="cell" id="9-2"></div>
            <div class="cell" id="9-3"></div>
            <div class="cell" id="9-4"></div>
            <div class="cell" id="9-5"></div>
            <div class="cell" id="9-6"></div>
            <div class="cell" id="9-7"></div>
            <div class="cell" id="9-8"></div>
            <div class="cell" id="9-9"></div>
        </div>

        <div class="inventory">
            <div class="slot" id="slot1"></div>
            <div class="slot" id="slot2"></div>
            <div class="slot" id="slot3"></div>
            <div class="slot" id="slot4"></div>
            <div class="slot" id="slot5"></div>
        </div>

        <button class="discard" id="discard">Descartar</button>
    </main>

    <footer class="footer">
        <input type="text" name="nickname" id="nickname" class="nickname" placeholder="nickname">
        <button class="start" id="start">Start</button>
    </footer>
</body>
</html>
```

```
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

                    console.log(cell.effect);
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
```
```
.body {
    display: flex;
    flex-direction: column;
    align-items: center;
    max-height: 100vw;
    height: 100vw;
    padding: 1rem;
    box-sizing: border-box;
    background-color: #f0f0f0;
    gap: 1rem;
}

.header {
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 1rem;
    box-sizing: border-box;
}

.title {
    margin: 0;
}

.main {
    display: flex;
    justify-content: center;
    flex-direction: column;
    align-items: center;
    padding: 1rem;
    box-sizing: border-box;
    width: min-content;
    aspect-ratio: 1 / 1;
    gap: 1rem;
}

.turn {
    padding: 1rem;
    box-sizing: border-box;
    display: flex;
    justify-content: center;
    align-items: center;
    margin: 0;
    width: 100%;
}

.words {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 1rem;
    box-sizing: border-box;
    gap: 1rem;
}

.word {
    padding: 0.5rem;
    margin: 0;
    font-size: 2rem;
    text-transform: uppercase;
}

.wordMe {
    padding: 0.5rem;
    margin: 0;
    font-size: 2rem;
    text-transform: uppercase;
    background-color: rgb(65, 165, 190);
    color: #fff;
    border-radius: 0.5rem;
}

.wordOther {
    padding: 0.5rem;
    margin: 0;
    font-size: 2rem;
    text-transform: uppercase;
    background-color: rgb(252, 198, 98);
    border-radius: 0.5rem;
}

.grid {
    display: grid;
    grid-template-columns: repeat(10, 1fr);
    justify-items: center;
    align-items: center;
    padding: 0.5rem;
    background-color: #9e9e9e;
    gap: 0.5rem;
}

.cell {
    background-color: #f5f5f5;
    display: flex;
    box-sizing: border-box;
    padding: 0.3rem;
    justify-content: center;
    align-items: center;
    width: 32px;
    height: 32px;
    cursor: pointer;
}

.inventory {
    display: flex;
    gap: 10px;
    justify-content: center;
    margin-top: 20px;
}

.slot {
    width: 80px;
    height: 80px;
    border: 2px dashed #ccc;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 10px;
    text-align: center;
    text-transform: uppercase;
    background-color: #f9f9f9;
    border-radius: 8px;
    transition: all 0.3s ease;
}

.slot.has-power {
    border: 2px solid #2ecc71;
    background-color: #eafaf1;
    font-weight: bold;
    color: #27ae60;
}

.slot.selected {
    background-color: #2e80cc;
}

.discard {
    display: flex;
    justify-content: center;
    align-items: center;
    box-sizing: border-box;
    padding: 0.4rem 1rem;
    cursor: pointer;
}

.footer {
    display: flex;
    justify-content: center;
    align-items: center;
    box-sizing: border-box;
    padding: 1rem;
    gap: 1rem;
}

.nickname {
    font-size: 1.3rem;
}

.start {
    display: flex;
    justify-content: center;
    align-items: center;
    box-sizing: border-box;
    padding: 0.4rem 1rem;
    cursor: pointer;
}

.hide {
    display: none;
}
```