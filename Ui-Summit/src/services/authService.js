import { store } from "../state/store.js";

const httpUrl = "http://localhost:8080";

export const AuthService = {
    async registerAndLogin(name) {
        console.log("Iniciando Cadastro...");
        try {
            await fetch(`${httpUrl}/user`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ email: `${name}@gmail.com`, password: "12345678" })
            });

            console.log("Iniciando Login...");
            const res = await fetch(`${httpUrl}/user/login`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ email: `${name}@gmail.com`, password: "12345678" })
            }).then(r => r.json());

            if (res.data) {
                store.state.user = { id: res.data.id, token: res.data.token, name: name };
                console.log("Logado com sucesso! ID:", res.data.id);
                return true;
            }
        } catch (err) {
            console.error("Erro na autenticação:", err);
        }
        return false;
    }
};