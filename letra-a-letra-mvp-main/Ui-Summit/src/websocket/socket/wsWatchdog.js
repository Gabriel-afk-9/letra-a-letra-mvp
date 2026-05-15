export const wsWatchdog = {
    timer: null,

    reset(onExpire) {
        this.stop();
        this.timer = setTimeout(onExpire, 50000);
    },

    stop() {
        if (this.timer) clearTimeout(this.timer);
    }
};