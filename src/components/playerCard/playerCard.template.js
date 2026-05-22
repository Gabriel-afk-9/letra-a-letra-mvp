export function createPowerDots(count, max = 5) {
    return Array.from({ length: max }, (_, i) => {
        const filled = i < count ? "filled" : "empty";
        return `<span class="dot ${filled}"></span>`;
    }).join("");
}