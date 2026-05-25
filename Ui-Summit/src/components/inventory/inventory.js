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
        this.orderedInventory = [];
        this.unsubscribes = [];
        this.hasGlobalListeners = false;
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

        if (!this.hasGlobalListeners) {
            this.setupGlobalListeners();
            this.hasGlobalListeners = true;
        }
        this.registerSubscriptions();

        this.render();
    }

   
    registerSubscriptions() {
        if (this.unsubscribes.length > 0) return;

        this.unsubscribes.push(...registerInventorySubscriptions(this));

        
        this.unsubscribes.push(store.subscribe('players', () => {
            this.updateOrderedInventory();
            this.render();
        }));
    }

    
    updateOrderedInventory() {
        const players = store.state.players;
        if (!players?.length) return;
        
        const me = players.find(p => p.id === store.state.user.id);
        if (!me || !me.inventory) return;

        const serverPowers = me.inventory.filter(p => p !== null && p !== undefined);

        
        this.orderedInventory = this.orderedInventory.filter(localPower => 
            serverPowers.some(serverPower => serverPower.id === localPower.id)
        );

        
        serverPowers.forEach(serverPower => {
            const isAlreadyInQueue = this.orderedInventory.some(localPower => localPower.id === serverPower.id);
            if (!isAlreadyInQueue) {
                this.orderedInventory.push({ ...serverPower, isNew: true });
            }
        });
    }

    render() {
        

        let html = `
            <div class="inventory-container">
                <div class="inventory-box">
        `;

        const getRarityClass = (powerName) => {
            const rarities = {
                "BLOCK": "rarity-common",
                "UNBLOCK": "rarity-common",
                "TRAP": "rarity-common",
                "DETECT_TRAPS": "rarity-common",
                "SPY": "rarity-rare",
                "FREEZE": "rarity-rare",
                "UNFREEZE": "rarity-rare",
                "BLIND": "rarity-epic",
                "LANTERN": "rarity-epic",
                "IMMUNITY": "rarity-legendary"
            };
            return rarities[powerName] || "rarity-common";
        };

        
        for (let i = 0; i < 5; i++) {
            const power = this.orderedInventory[i];

            if (power) {
                const animClass = power.isNew ? 'anim-enter' : '';
                power.isNew = false;
                
                const rarityClass = getRarityClass(power.name);

                html += createPowerSlot({
                    power,
                    isSelected: store.state.activePower?.id === power.id ? 'selected' : '',
                    isDisabled: (this.isFrozen() && !PowerRulesService.canUseWhileFrozen(power.name)) ? 'power-disabled' : '',
                    icon:       PowerRulesService.getIcon(power.name),
                    scope:      PowerRulesService.getScope(power.name),
                    animClass:  animClass,
                    rarityClass: rarityClass
                });
            } else {
                html += createEmptySlot();
            }
        }

        html += `
                </div>
            </div>
        `;
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

        let isDragging = false;
        let draggedCard = null;

        const handleStart = (y, card) => { if (card) this.startY = y;
            isDragging = true;
                draggedCard = card;
         };
         const handleMove = (y) => {
            if (!isDragging || !draggedCard) return;
            
            const diffY = this.startY - y;

            if (diffY < -20) {
                
                draggedCard.classList.add('is-discarding');
                draggedCard.classList.remove('is-using');
            } 
            else if (diffY > 20 && draggedCard.dataset.scope === "GLOBAL") {
                
                draggedCard.classList.add('is-using');
                draggedCard.classList.remove('is-discarding');
            } 
            else {
                
                draggedCard.classList.remove('is-discarding', 'is-using');
            }
        };
        const handleEnd = (y) => {
            if (isDragging && draggedCard) {
                const diffY = this.startY - y;
                const { id, type, scope } = draggedCard.dataset;
                
                
                draggedCard.classList.remove('is-discarding', 'is-using');
                
                
                handlers.handleSwipe(this, id, type, scope, diffY);
                
                
                isDragging = false;
                draggedCard = null;
            }
        };

        this.addEventListener('touchstart', (e) => handleStart(e.touches[0].clientY, e.target.closest('.power-card')));
        this.addEventListener('touchmove', (e) => handleMove(e.touches[0].clientY));
        this.addEventListener('touchend', (e) => handleEnd(e.changedTouches[0].clientY));

        this.addEventListener('mousedown', (e) => handleStart(e.clientY, e.target.closest('.power-card')));
        this.addEventListener('mousemove', (e) => handleMove(e.clientY));
        this.addEventListener('mouseup', (e) => handleEnd(e.clientY));
        this.addEventListener('mouseleave', (e) => handleEnd(e.clientY));
    }

    disconnectedCallback() {
        this.unsubscribes.forEach(unsubscribe => unsubscribe());
        this.unsubscribes = [];
    }
}

customElements.define("inventory-component", InventoryComponent);
