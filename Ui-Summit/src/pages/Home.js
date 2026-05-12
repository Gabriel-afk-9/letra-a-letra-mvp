// class HomePage extends HTMLElement {
//     connectedCallback() {
//         this.innerHTML = `
//             <div class="container">
//                 <img src="public/logo2.png" alt="Logo Letra a Letra" class="logo" />

//                 <p class="label">Insira seu Nome</p>

//                 <input
//                     type="text"
//                     placeholder="ex: Player123..."
//                     class="input"
//                     id="name"
//                 />

//                 <button class="button" id="play">Jogar</button>
//             </div>
//         `;
//     }
// }

// customElements.define("home-page", HomePage);

import { AuthService } from "../services/authService.js";
import { store } from "../state/store.js";

class HomePage extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
            <div class="container">
                <img src="public/logo2.png" alt="Logo Letra a Letra" class="logo" />
                <p class="label">Insira seu Nome</p>
                <input type="text" placeholder="ex: Player123..." class="input" id="name" />
                <button class="button" id="play">Jogar</button>
            </div>
        `;

        const playBtn = this.querySelector("#play");
        const nameInput = this.querySelector("#name");

        playBtn.addEventListener("click", async () => {
            const testName = nameInput.value.trim();

            if (testName.length < 4 || testName.length > 15) {
                alert("O nickname deve ter entre 4 e 15 caracteres.");
                return;
            }

            playBtn.disabled = true;
            playBtn.innerText = "Conectando...";

            const success = await AuthService.registerAndLogin(testName);

            if (success) {
                // Ao dar sucesso, só mudamos o estado. O sistema reage automaticamente!
                store.state.currentPage = 'matchmaking';
            } else {
                alert("Erro ao realizar login. Tente novamente.");
                playBtn.disabled = false;
                playBtn.innerText = "Jogar";
            }
        });
    }
}
customElements.define("home-page", HomePage);