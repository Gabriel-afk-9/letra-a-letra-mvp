// class MatchmakingPage extends HTMLElement {
//     connectedCallback() {
//         this.innerHTML = `
//             <div class="matchmaking-container">
//                 <div id="searching-state">
//                     <h1 id="mm-title">Buscando Oponente...</h1>
//                     <div id="mm-spinner" class="spinner"></div>
//                 </div>
                
//                 <div id="found-state" style="display: none;">
//                     <h2>Oponente Encontrado!</h2>
//                     <p class="vs-text">
//                         <span id="my-name"></span> <strong>VS</strong> <span id="opponent-name"></span>
//                     </p>
//                     <p>Preparando a partida...</p>
//                 </div>
//             </div>
//         `;
//     }

//     showOpponent(myName, opponentName) {
//         document.getElementById("searching-state").style.display = "none";
//         document.getElementById("found-state").style.display = "block";
        
//         document.getElementById("my-name").innerText = myName;
//         document.getElementById("opponent-name").innerText = opponentName;
//     }
// }

// customElements.define("matchmaking-page", MatchmakingPage);

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