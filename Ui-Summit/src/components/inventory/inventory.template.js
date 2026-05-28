export function createPowerSlot({ power, isSelected, isDisabled, icon, scope, animClass ='',rarityClass = '' }) {
    return `
        <div class="slot has-power power-card ${isSelected} ${isDisabled} ${animClass} ${rarityClass}"
             data-id="${power.id}"
             data-type="${power.name}"
             data-scope="${scope}">
            <img src="${icon}" alt="${power.name}" class="power-icon" draggable="false" />
        </div>
    `;
}

export function createEmptySlot() {
    return `<div class="slot"></div>`;
}

// 👇 Botões deletados! Agora a função não renderiza mais nada na tela.
export function createActionButtons(activePower) {
    return ''; 
}