import { AppConfig } from "../../config/app.config.js";

export const wsWatchdog = {
    timer: null,

    reset(onExpire) {
        this.stop();
        this.timer = setTimeout(onExpire, AppConfig.TIMEOUTS.WATCHDOG_MS);
    },

    stop() {
        if (this.timer) clearTimeout(this.timer);
    }
};