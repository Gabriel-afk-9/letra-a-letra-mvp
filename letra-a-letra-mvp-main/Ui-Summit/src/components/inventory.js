import { store } from "../state/store.js";

export class InventoryComponent extends HTMLElement {
    connectedCallback() {
        this.className = "inventory-section";
        
        if (store.state.selectedPower === undefined) store.state.selectedPower = null;

        store.subscribe('players', (players) => this.render(players));
        store.subscribe('selectedPower', () => this.render(store.state.players));

        this.addEventListener('click', (e) => {
            const slot = e.target.closest('.slot.has-power');
            if (slot) {
                const powerId = slot.dataset.powerId;
                const powerName = slot.dataset.powerName;
                
                if (store.state.selectedPower?.id === powerId) {
                    store.state.selectedPower = null;
                } else {
                    store.state.selectedPower = { id: powerId, name: powerName };
                }
            }

            if (e.target.id === 'discard-btn') {
                this.dispatchEvent(new CustomEvent('discard-power', {
                    detail: { powerId: store.state.selectedPower.id },
                    bubbles: true
                }));
            }
        });

        this.render(store.state.players);
    }

    render(players) {
        if (!players || players.length === 0) return;

        const me = players.find(p => p.id === store.state.user.id);
        if (!me) return;

        let html = '<div class="inventory">';
        
        for (let i = 0; i < 5; i++) {
            const power = me.inventory && me.inventory[i];
            
            if (power) {
                const isSelected = store.state.selectedPower?.id === power.id ? 'selected' : '';
                html += `
                    <div class="slot has-power ${isSelected}" 
                         data-power-id="${power.id}" 
                         data-power-name="${power.name}">
                        ${power.name}
                    </div>
                `;
            } else {
                html += `<div class="slot"></div>`;
            }
        }
        html += '</div>';

        if (store.state.selectedPower) {
            html += `<button id="discard-btn" class="discard button">Descartar</button>`;
        }

        this.innerHTML = html;
    }
}
customElements.define("inventory-component", InventoryComponent);