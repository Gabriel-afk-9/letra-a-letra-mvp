const SERVER_HOST = "10.62.125.44:8080";

export const AppConfig = {
    API_URL: `http://${SERVER_HOST}`,
    WS_URL: `ws://${SERVER_HOST}/ws/game`,
    TIMEOUTS: {
        WATCHDOG_MS: 50_000,
        TURN_FALLBACK_MS: 30_000
    }
};