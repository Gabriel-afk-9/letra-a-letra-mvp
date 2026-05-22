const animatedCells = new Set();

export function resolveCellStyles(cell, state) {
    const { user, isBlind, canDetectTraps } = state;
    const isEffectMine = cell.effectOwner === user?.id;
    const cellKey = `${cell.x},${cell.y}`;

    let classes = ["cell", cell.revealed ? "revealed" : "hidden"];
    let content = `<span>${cell.letter}</span>`;

    if (cell.revealed) {
        if (!animatedCells.has(cellKey)) {
            classes.push("animate-reveal");
            animatedCells.add(cellKey);
        }

        if (isBlind) {
            classes.push("cell-blinded");
            content = `<span>?</span>`;
        } 
        else if (cell.foundBy) {
            classes.push(cell.foundBy === user?.id ? "found-me" : "found-opponent");
        } 
        else if (cell.revealedBy) {
            classes.push(cell.revealedBy === user?.id ? "revealed-me" : "revealed-opponent");
        }
    }

    if (cell.effectType === "BLOCK") {
        classes.push(isEffectMine ? "block-me" : "block-opponent");
        content += `<div class="padlock-icon">🔒 ${cell.remainingClicks}</div>`;
    } 
    else if (cell.effectType === "TRAP") {
        if (isEffectMine) {
            classes.push("trap-me");
        } else if (canDetectTraps) {
            classes.push("trap-detected");
            content += `<div class="trap-icon">⚠️</div>`;
        }
    } 
    else if (cell.effectType === "SPY" && isEffectMine) {
        classes.push("cell-spied");
        content = `<span>${cell.letter}</span>`;
    }

    return {
        classes,
        content
    };
}