export function createCell(cell) {
    return `
        <cell-component
            x="${cell.x}"
            y="${cell.y}"
            letter="${cell.letter}"
            revealed="${cell.revealed}"
            revealed-by="${cell.revealedBy}"
            found-by="${cell.foundBy}"
            effect-type="${cell.effectType}"
            effect-owner="${cell.effectOwner}"
            remaining-clicks="${cell.remainingClicks}">
        </cell-component>
    `;
}