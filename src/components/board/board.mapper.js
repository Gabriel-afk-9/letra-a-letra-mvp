export function mapBoardCells(boardData, foundMap) {
    const cells = [];

    boardData.forEach((row, x) => {
        row.forEach((cellData, y) => {
            cells.push({
                x,
                y,
                letter: cellData.letter || "",
                revealed: cellData.revealed,
                revealedBy: cellData.revealedBy || "",
                foundBy: foundMap[`${x},${y}`] || "",
                effectType: cellData.effect?.effect || "",
                effectOwner: cellData.effect?.ownerId || "",
                remainingClicks: cellData.effect?.remainingClicks || ""
            });
        });
    });

    return cells;
}