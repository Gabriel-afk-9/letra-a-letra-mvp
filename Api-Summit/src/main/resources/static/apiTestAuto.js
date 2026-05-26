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
      await sleep(50000);

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