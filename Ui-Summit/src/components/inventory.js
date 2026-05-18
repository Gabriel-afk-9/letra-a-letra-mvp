import { store } from "../state/store.js";
import { GameService } from "../services/game/gameService.js";

const GLOBAL_POWERS = ["FREEZE", "UNFREEZE", "BLIND", "LANTERN", "IMMUNITY", "DETECT_TRAPS"];

const POWER_ICONS = {
    "FREEZE": "assets/powers/icon-freeze.png",
    "UNFREEZE": "assets/powers/icon-unfreeze.png",
    "BLIND": "assets/powers/icon-blind.png",
    "LANTERN": "assets/powers/icon-lantern.png",
    "IMMUNITY": "assets/powers/icon-imunity.png",
    "DETECT_TRAPS": "assets/powers/icon-detecttraps.png",
    "BLOCK": "assets/powers/icon-block.png",
    "SPY": "assets/powers/icon-spy.png",
    "TRAP": "assets/powers/icon-trap.png"
};

export class InventoryComponent extends HTMLElement {
    connectedCallback() {
        this.className = "inventory-section";

        store.subscribe('players', () => this.render());
        store.subscribe('activePower', () => this.render());
        store.subscribe('playerEffects', () => {
            if (store.state.playerEffects?.freeze && store.state.activePower?.type !== "UNFREEZE") {
                store.state.activePower = null;
                document.body.className = "";
            }
            this.render();
        });

        this.addEventListener('click', (e) => {
            if (!store.state.activePower) return;

            if (e.target.id === 'discard-btn') {
                GameService.discardPower(store.state.activePower.id);
                store.state.activePower = null;
                document.body.className = "";
            } 
            else if (e.target.id === 'use-btn') {
                if (store.state.playerEffects?.freeze) {
                    if (store.state.activePower.type !== "UNFREEZE") {
                        store.state.notification = { 
                            message: "Você está congelado! Só pode usar o poder UNFREEZE.", 
                            type: "me" 
                        };
                        return;
                    }
                }                
                
                GameService.playGlobalPower(store.state.activePower.id, store.state.activePower.type);
                store.state.activePower = null;
                document.body.className = "";
            }
        });

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
                const scope = GLOBAL_POWERS.includes(power.name) ? "GLOBAL" : "CELL";
                const isSelected = store.state.activePower?.id === power.id ? 'selected' : '';
                const isFrozen = store.state.playerEffects?.freeze;
                const isDisabled = (isFrozen && power.name !== "UNFREEZE") ? 'power-disabled' : '';
                //Mudar dps
                const imgSrc = POWER_ICONS[power.name] || "./assets/powers/default.png";
                
                html += `
                    <div class="slot has-power power-card ${isSelected} ${isDisabled}"
                         data-id="${power.id}"
                         data-type="${power.name}"
                         data-scope="${scope}">
                         <img src="${imgSrc}" alt="${power.name}" class="power-icon" draggable="false" />
                    </div>
                `;
            } else {
                html += `<div class="slot"></div>`;
            }
        }

        html += '</div>';

        if (store.state.activePower) {
            html += `<div class="active-power-actions" style="display: flex; gap: 10px; justify-content: center; margin-top: 15px;">`;
            
            if (store.state.activePower.scope === "GLOBAL") {
                html += `<button id="use-btn" class="use button" style="background-color: #409ddb;">Usar Poder</button>`;
            }
            
            html += `<button id="discard-btn" class="discard button" style="background-color: #e74c3c;">Descartar</button>`;
            html += `</div>`;
        }

        this.innerHTML = html;
        this.attachGestures();
    }

    attachGestures() {
        this.querySelectorAll('.power-card').forEach(card => {
            let startY = 0;
            const { id: powerId, type: powerType, scope } = card.dataset;

            card.addEventListener('click', () => {
                if (store.state.playerEffects?.freeze && powerType !== "UNFREEZE") {
                    return; 
                }
                const isAlreadySelected = store.state.activePower?.id === powerId;

                if (isAlreadySelected) {
                    store.state.activePower = null;
                    document.body.className = "";
                    return;
                }

                store.state.activePower = { id: powerId, type: powerType, scope };
                document.body.className = scope === "CELL" ? "target-cell-mode" : "target-global-mode";
            });

            const handleSwipe = (diffY) => {
                if (store.state.playerEffects?.freeze && powerType !== "UNFREEZE") {
                    return; 
                }
                if (diffY > 50) {
                    if (scope === "GLOBAL") {
                        GameService.playGlobalPower(powerId, powerType);
                        store.state.activePower = null;
                        document.body.className = "";
                    } else {
                        store.state.notification = {
                            message: "Selecione uma célula no tabuleiro!",
                            type: "me"
                        };
                    }
                } else if (diffY < -50) {
                    GameService.discardPower(powerId);
                    store.state.activePower = null;
                    document.body.className = "";
                }
            };

            card.addEventListener('touchstart', (e) => {
                startY = e.touches[0].clientY;
            });

            card.addEventListener('touchend', (e) => {
                const diffY = startY - e.changedTouches[0].clientY;
                handleSwipe(diffY);

            });

            card.addEventListener('mousedown', (e) => {
                startY = e.clientY;
            });

            card.addEventListener('mouseup', (e) => {
                const diffY = startY - e.clientY;
                handleSwipe(diffY);
            });
        });
    }
}

customElements.define("inventory-component", InventoryComponent);