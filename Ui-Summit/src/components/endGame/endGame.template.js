export function createEndGameTemplate() {
    return `
        <div id="end-overlay" class="end-overlay hidden">
            <div class="end-modal" id="end-modal">
                <h1 id="end-title"></h1>
                <p id="end-message"></p>
                <button id="btn-home">Voltar ao Início</button>
            </div>
        </div>
    `;
}