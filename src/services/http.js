<<<<<<< HEAD:Ui-Summit/src/services/http.js
const BASE_URL = "http://:8080";
=======
const BASE_URL = "http://10.103.171.5:8080";
>>>>>>> 95b97aa07fa2171e508311b345a25e0364d8cd52:src/services/http.js

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