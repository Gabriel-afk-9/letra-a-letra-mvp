import { AuthService } from "../services/auth/authService.js";
import { store } from "../state/store.js";

class HomePage extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
            <div class="container">
            <div id="login-error-popup" class="login-error-popup">
                    Erro ao conectar. Tente novamente.
                </div>
                <div class="card">
                    <img src="public/logo2.png" alt="Logo Letra a Letra" class="logo" />
                    <p class="label">Insira seu Nome</p>
                    <input type="text" maxlength=10 placeholder="No mínimo 5 letras.." class="input" id="name" autocomplete="off" />
                    <button class="button" id="play">Jogar</button>
                </div>
            </div>
        `;

        const playBtn = this.querySelector("#play");
        const nameInput = this.querySelector("#name");
        const errorPopup = this.querySelector("#login-error-popup");

        playBtn.addEventListener("click", async () => {
            playBtn.classList.add("btn-clicked");
            setTimeout(() => playBtn.classList.remove("btn-clicked"), 150);

            const testName = nameInput.value.trim();

            if (testName.length < 5|| testName.length > 10) {
                nameInput.classList.add("input-error");
                
                setTimeout(() => {
                    nameInput.classList.remove("input-error");
                }, 400);

                return;
            }

            playBtn.disabled = true;
            playBtn.innerText = "Conectando...";

            const success = await AuthService.registerAndLogin(testName);

            if (success) {
                store.state.currentPage = 'matchmaking';
            } else {
                errorPopup.classList.add("show");
                
                setTimeout(() => {
                    errorPopup.classList.remove("show");
                }, 3500);

                playBtn.disabled = false;
                playBtn.innerText = "Jogar";
            }
        });
    }
}
customElements.define("home-page", HomePage);