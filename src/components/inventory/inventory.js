import { store } from "../../state/store.js";
import { PowerRulesService } from "../../services/game/powerRulesService.js";
import { UiModeService } from "../../services/ui/uiModeService.js";
import { createPowerSlot, createEmptySlot, createActionButtons } from "./inventory.template.js";
import { registerInventorySubscriptions } from "./inventory.subscriptions.js";
import * as handlers from "./inventory.handlers.js";

export class InventoryComponent extends HTMLElement {
    constructor() {
        super();
        this.startY = 0;
    }
    
    isFrozen() {
        return !!store.state.playerEffects?.freeze;
    }

    clearSelection() {
        store.state.activePower = null;
        UiModeService.clear();
    }

    connectedCallback() {
        this.className = "inventory-section";

        this.setupGlobalListeners();
        registerInventorySubscriptions(this);

        this.render();
    }

    render() {
        const players = store.state.players;
        if (!players?.length) return;

        const me = players.find(p => p.id === store.state.user.id);
        if (!me) return;

        let html = '<div class="inventory">';

        for (let i = 0; i < 5; i++) {
            const power = me.inventory?.[i];

            if (power) {
                html += createPowerSlot({
                    power,
                    isSelected: store.state.activePower?.id === power.id ? 'selected' : '',
                    isDisabled: (this.isFrozen() && !PowerRulesService.canUseWhileFrozen(power.name)) ? 'power-disabled' : '',
                    icon:       PowerRulesService.getIcon(power.name),
                    scope:      PowerRulesService.getScope(power.name)
                });
            } else {
                html += createEmptySlot();
            }
        }

        html += '</div>';
        html += createActionButtons(store.state.activePower);

        this.innerHTML = html;
    }

    setupGlobalListeners() {
        this.addEventListener('click', (e) => {
            const target = e.target;

            if (target.id === 'discard-btn') {
                return handlers.handleDiscardBtn(this, store.state.activePower);
            }
            if (target.id === 'use-btn') {
                return handlers.handleUseBtn(this, store.state.activePower);
            }

            const card = target.closest('.power-card');
            if (card) {
                handlers.handleCardClick(this, card);
            }
        });

        const handleStart = (y, card) => { if (card) this.startY = y; };
        const handleEnd = (y, card) => {
            if (card) {
                const { id, type, scope } = card.dataset;
                handlers.handleSwipe(this, id, type, scope, this.startY - y);
            }
        };

        this.addEventListener('touchstart', (e) => handleStart(e.touches[0].clientY, e.target.closest('.power-card')));
        this.addEventListener('touchend', (e) => handleEnd(e.changedTouches[0].clientY, e.target.closest('.power-card')));
        this.addEventListener('mousedown', (e) => handleStart(e.clientY, e.target.closest('.power-card')));
        this.addEventListener('mouseup', (e) => handleEnd(e.clientY, e.target.closest('.power-card')));
    }
}

customElements.define("inventory-component", InventoryComponent);