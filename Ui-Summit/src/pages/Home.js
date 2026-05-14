import { AuthService } from "../services/authService.js";
import { store } from "../state/store.js";

class HomePage extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
            <div class="container">
                <div class="card">
                    <img src="public/logo2.png" alt="Logo Letra a Letra" class="logo" />
                    <p class="label">Insira seu Nome</p>
                    <input type="text" maxlength=10 placeholder="ex: Player123..." class="input" id="name" />
                    <button class="button" id="play">Jogar</button>
                </div>
            </div>
        `;

        const playBtn = this.querySelector("#play");
        const nameInput = this.querySelector("#name");

        playBtn.addEventListener("click", async () => {
            const testName = nameInput.value.trim();

            if (testName.length < 3|| testName.length > 10) {
                alert("O nickname deve ter entre 3 e 15 caracteres.");
                return;
            }

            playBtn.disabled = true;
            playBtn.innerText = "Conectando...";

            const success = await AuthService.registerAndLogin(testName);

            if (success) {
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