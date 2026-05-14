import { store } from "../state/store.js";
import "../components/player.js";
import "../components/words.js";
import "../components/board.js";
import "../components/inventory.js";
import "../pages/Home.js";
import "../pages/Game.js";
import "../pages/Matchmaking.js";
import "../pages/Found.js";
import "../components/active-player.js";

const root = document.body;

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

store.state.currentPage = 'home';