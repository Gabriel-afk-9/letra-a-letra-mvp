import { store } from "../state/store.js";
import { GameService } from "../services/game/gameService.js"
import "../components/playerCard/playerCard.js";
import "../components/words.js";
import "../components/board/board.js";
import "../components/inventory/inventory.js";
import "../components/cell/cell.js";
import "../components/notification.js";
import "../components/endGame/endGame.js";
import "../pages/Home.js";
import "../pages/Game.js";
import "../pages/Matchmaking.js";
import "../pages/Found.js";

const root = document.getElementById('app');

store.subscribe('currentPage', (page) => {
    root.classList.add('fade-out');

    setTimeout(() => {
        if (page === "home") {
            root.innerHTML = '<home-page></home-page>';
        } else if (page === "matchmaking") {
            root.innerHTML = '<matchmaking-page></matchmaking-page>';
        } else if (page === "found") {
            root.innerHTML = '<found-page></found-page>';
        } else if (page === "game") {
            root.innerHTML = '<game-page></game-page>';
        }

        root.classList.remove('fade-out');
    }, 300);
});

window.addEventListener('beforeunload', () => {
    if (store.state.tokenGameId) {
        GameService.leaveGame(true);
    }
});

store.state.currentPage = 'home';