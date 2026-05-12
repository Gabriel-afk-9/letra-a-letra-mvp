// import "../components/player.js";
// import "../components/words.js";
// import "../components/board.js";
// import "../components/inventory.js";
// import "../../src/pages/Home.js";
// import "../../src/pages/Game.js";
// import "../../src/pages/Matchmaking.js";

// let httpUrl = "http://localhost:8080"
// let wsUrl = "ws://localhost:8080/ws/game?token="

// let id;
// let token;

// async function register(name) {
//     console.log("Iniciando Cadastro...");

//     try {
//         const res = await fetch(`${httpUrl}/user`, {
//             method: "POST",
//             headers: { "Content-Type": "application/json" },
//             body: JSON.stringify({
//                 email: `${name}@gmail.com`,
//                 password: "12345678"
//             })
//         }).then(res=>res.json());
//         console.log(res);

//     } catch (err) {
//         console.error(err);
//     }
// }

// async function login(name) {
//     console.log("Iniciando Login...");

//     try {
//         const res = await fetch(`${httpUrl}/user/login`, {
//             method: "POST",
//             headers: { "Content-Type": "application/json" },
//             body: JSON.stringify({
//                 email: `${name}@gmail.com`,
//                 password: "12345678"
//             })
//         }).then(res=>res.json());
//         console.log(res);

//         if (res.data) {
//             id = res.data.id;
//             token = res.data.token;
//             console.log("Logado com sucesso! ID:", id);
//         } else {
//             console.error("Login falhou, formato inesperado:", res);
//         }

//     } catch (err) {
//         console.error(err);
//     }
// }

// export function navigate(page) {
//     const root = document.body;

//     if (page == "home") {
//         root.innerHTML = '<home-page></home-page>';

//     } else {
//         root.innerHTML = '<game-page></game-page>';
//     }
// }

// navigate("home");

// export let gameWs = null; 

// function startMatchmaking(token, userId, myName) {
//     const root = document.body;

//     root.innerHTML = `
//         <div class="matchmaking-container">
//             <h1 id="mm-title">Buscando Oponente...</h1>
//             <div id="mm-spinner" class="spinner"></div>
            
//             <div id="mm-found" class="matchmaking-hide">
//                 <h2>Oponente Encontrado!</h2>
//                 <p class="vs-text">${myName} <strong>VS</strong> <span id="opponent-name">???</span></p>
//                 <p>Preparando a partida...</p>
//             </div>
//         </div>
//     `;

//     gameWs = new WebSocket(`${wsUrl}${token}`);

//     gameWs.onopen = () => {
//         console.log("WebSocket Aberto. Pedindo partida...");
        
//         gameWs.send(JSON.stringify({
//             type: "MATCHMAKING_GAME",
//             gameMode: "NORMAL"
//         }));
//     };

//     gameWs.onmessage = (event) => {
//         const msg = JSON.parse(event.data);
//         console.log("WS Recebido no Matchmaking:", msg);

//         if (msg.event === "MATCHMAKING_GAME" && msg.status === "FOUNDED") {
            
//             let opponentName = "Desafiante";
//             if (msg.data && msg.data.players) {
//                 const opponent = msg.data.players.find(p => p.id !== userId);
//                 if (opponent) opponentName = opponent.nickname;
//             }

//             document.getElementById("mm-title").classList.add("matchmaking-hide");
//             document.getElementById("mm-spinner").classList.add("matchmaking-hide");
            
//             document.getElementById("mm-found").classList.remove("matchmaking-hide");
//             document.getElementById("opponent-name").innerText = opponentName;

//             setTimeout(() => {
//                 navigate("game");
//             }, 4000);
//         }
//     };

//     gameWs.onerror = (err) => {
//         console.error("Erro no WebSocket:", err);
//         alert("Erro ao conectar no servidor do jogo.");
//         navigate("home");
//     };
// }

// const play = document.getElementById("play");

// play.addEventListener("click", async () => {
//     const nameInput = document.getElementById("name");
//     const testName = nameInput.value.trim();

//     if (testName.length < 4 || testName.length > 15) {
//         alert("O nickname deve ter entre 5 e 15 caracteres.");
//         return;
//     }

//     play.disabled = true;
//     play.innerText = "Conectando...";

//     await register(testName);
//     await login(testName);

//     if (token && id) {
//         startMatchmaking(token, id, testName);
//     } else {
//         alert("Erro ao realizar login. Tente novamente.");
//         play.disabled = false;
//         play.innerText = "Jogar";
//     }
// });

// Importa os Web Components (para que o navegador os reconheça)
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