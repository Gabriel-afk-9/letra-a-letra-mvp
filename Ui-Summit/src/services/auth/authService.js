import { store, getAvatarHash } from "../../state/store.js";
import { http } from "../http.js";


const DEFAULT_PASSWORD = "12345678";
const toEmail = (name) => `${name}@gmail.com`;

export const AuthService = {
    async registerAndLogin(name) {
        try {
            await http.post("/user", {
                email: toEmail(name),
                password: DEFAULT_PASSWORD
            });

            const { data } = await http.post("/user/login", {
                email: toEmail(name),
                password: DEFAULT_PASSWORD
            });

            if (!data) return false;

            await http.patch(`/user/nickname/${data.id}`, {
                nickname: name
            }, data.token);

            store.state.user = {
                id: data.id,
                token: data.token,
                name,
                avatar: `assets/avatar/avatar-${getAvatarHash(data.id)}.png`
            };
            return true;

        } catch (err) {
            console.error("Erro na autenticação:", err);
            return false;
        }
    }
};