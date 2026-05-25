const BASE_URL = "http://192.168.18.18:8080";

export const http = {
    post: (path, body, token) => fetch(`${BASE_URL}${path}`, {
        method: "POST",
        headers: { 
            "Content-Type": "application/json",
            ...(token && { "Authorization": `Bearer ${token}` })
        },
        body: JSON.stringify(body)
    }).then(r => r.json()),

    patch: (path, body, token) => fetch(`${BASE_URL}${path}`, {
        method: "PATCH",
        headers: { 
            "Content-Type": "application/json",
            ...(token && { "Authorization": `Bearer ${token}` })
        },
        body: JSON.stringify(body)
    }).then(r => r.json()),
};