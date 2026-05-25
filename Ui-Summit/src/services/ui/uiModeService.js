const TARGET_MODE_CLASSES = ["target-cell-mode", "target-global-mode"];
const GAME_EFFECT_CLASSES = ["is-frozen", "global-blind-effect"];

export const UiModeService = {
    clear() {
        document.body.classList.remove(...TARGET_MODE_CLASSES);
    },

    clearGameClasses() {
        document.body.classList.remove(...TARGET_MODE_CLASSES, ...GAME_EFFECT_CLASSES);
    },

    setTargetMode(scope) {
        this.clear();
        document.body.classList.add(scope === "CELL"
            ? "target-cell-mode"
            : "target-global-mode"
        );
    }
};
