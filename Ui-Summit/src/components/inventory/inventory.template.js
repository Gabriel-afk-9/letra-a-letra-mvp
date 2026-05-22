export function createPowerSlot({ power, isSelected, isDisabled, icon, scope }) {
    return `
        <div class="slot has-power power-card ${isSelected} ${isDisabled}"
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

export function createActionButtons(activePower) {
    if (!activePower) return '';

    let html = `<div class="active-power-actions">`;
    if (activePower.scope === "GLOBAL") {
        html += `<button id="use-btn" class="use button">Usar Poder</button>`;
    }
    html += `<button id="discard-btn" class="discard button">Descartar</button>`;
    html += `</div>`;
    
    return html;
}