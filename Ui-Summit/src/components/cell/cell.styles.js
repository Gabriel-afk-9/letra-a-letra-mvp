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
            content = `<span></span>`;
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
        const totalClicks = 3;
        const remaining = cell.remainingClicks || 3;
        const filled = totalClicks - remaining;

        content += `
            <div class="block-overlay">
                <div class="block-progress">
                    <div class="block-segment ${filled >= 1 ? 'filled' : ''}"></div>
                    <div class="block-segment ${filled >= 2 ? 'filled' : ''}"></div>
                    <div class="block-segment ${filled >= 3 ? 'filled' : ''}"></div>
                </div>
            </div>
        `;
    }

    else if (cell.effectType === "TRAP") {
        const trapImage =
            `<img src="assets/powers/icon-trap-cell.png" class="trap-img" alt="Armadilha" />`;
        ;

        if (isEffectMine) {
            classes.push("trap-me");
            content += trapImage;
        } 
        else if (canDetectTraps) {
            classes.push("trap-detected");
            content += trapImage;
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