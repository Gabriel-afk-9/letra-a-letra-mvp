export const UiModeService = {
    clear() {
        document.body.className = "";
    },

    setTargetMode(scope) {
        document.body.className = scope === "CELL" 
            ? "target-cell-mode" 
            : "target-global-mode";
    }
};