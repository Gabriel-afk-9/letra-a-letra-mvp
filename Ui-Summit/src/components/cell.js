import { store } from "../state/store.js";
import { GameService } from "../services/game/gameService.js";

const animatedCells = new Set();

const GLOBAL_POWERS = ["FREEZE", "UNFREEZE", "BLIND", "LANTERN", "IMMUNITY", "DETECT_TRAPS"];

export class CellComponent extends HTMLElement {
    connectedCallback() {
        const x = this.getAttribute('x');
        const y = this.getAttribute('y');
        const letter = this.getAttribute('letter') || '';
        const revealed = this.getAttribute('revealed') === 'true';
        const revealedBy = this.getAttribute('revealed-by');
        const foundBy = this.getAttribute('found-by');
        const effectType = this.getAttribute('effect-type');
        const effectOwner = this.getAttribute('effect-owner');
        const remainingClicks = this.getAttribute('remaining-clicks');

        const { playerEffects, user } = store.state;
        const isBlind = playerEffects?.blind;
        const canDetectTraps = playerEffects?.detect_traps;
        const isEffectMine = effectOwner === user.id;

        const cellKey = `${x},${y}`;
        let classes = `cell ${revealed ? 'revealed' : 'hidden'}`;
        let innerHtml = `<span>${letter}</span>`;

        if (revealed) {
            if (!animatedCells.has(cellKey)) {
                classes += " animate-reveal";
                animatedCells.add(cellKey);
            }

            if (isBlind) {
                classes += " cell-blinded";
                innerHtml = `<span>?</span>`;
            }
            else if (foundBy) {
                classes += foundBy === user.id ? " found-me" : " found-opponent";
            }
            else if (revealedBy) {
                classes += revealedBy === user.id ? " revealed-me" : " revealed-opponent";
            }
        }

        if (effectType === "BLOCK") {
            classes += isEffectMine ? " block-me" : " block-opponent";
            innerHtml += `<div class="padlock-icon">🔒 ${remainingClicks}</div>`;
        } else if (effectType === "TRAP") {
            if (isEffectMine) {
                classes += " trap-me";
            } else if (canDetectTraps) {
                classes += " trap-detected";
                innerHtml += `<div class="trap-icon">⚠️</div>`;
            }
        } else if (effectType === "SPY" && isEffectMine) {
            classes += " cell-spied";
            innerHtml = `<span>${letter}</span>`;
        }

        this.className = classes;
        this.innerHTML = innerHtml;

        this.addEventListener('click', () => {
 
            const isFreeze = store.state.playerEffects?.freeze;
            const isNotMyTurn = store.state.currentTurnPlayerId !== user.id;

            if (isNotMyTurn || isFreeze) {
                this.classList.add('shake-error');
                setTimeout(() => this.classList.remove('shake-error'), 400);

                if (isFreeze) {
                    store.state.notification = { message: "Você está congelado! 🧊 Use o UNFREEZE.", type: "me" };
                }
                return;
            }

            const { activePower } = store.state;

            if (activePower?.scope === "CELL") {
                GameService.playTurn(x, y, activePower.id, activePower.type);
                store.state.activePower = null;
                document.body.className = "";
                return;
            }

            if (activePower?.scope === "GLOBAL") {
                store.state.notification = {
                    message: "Este poder não precisa de célula! Arraste para cima.",
                    type: "me"
                };
                return;
            }

            GameService.playTurn(x, y);
        });
    }
}

customElements.define("cell-component", CellComponent);