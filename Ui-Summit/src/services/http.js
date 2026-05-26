import { AppConfig } from "../config/app.config.js";

export const http = {
    post: (path, body, token) => fetch(`${AppConfig.API_URL}${path}`, {
        method: "POST",
        headers: { 
            "Content-Type": "application/json",
            ...(token && { "Authorization": `Bearer ${token}` })
        },
        body: JSON.stringify(body)
    }).then(r => r.json()),

    patch: (path, body, token) => fetch(`${AppConfig.API_URL}${path}`, {
        method: "PATCH",
        headers: { 
            "Content-Type": "application/json",
            ...(token && { "Authorization": `Bearer ${token}` })
        },
        body: JSON.stringify(body)
    }).then(r => r.json()),
};