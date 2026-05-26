# 📘 Documentação da API

## 🔗 Visão Geral

- URL base HTTP: `http://<host>:8080`
- URL base WebSocket: `ws://<host>:8080/ws/game?token=<USER_JWT>`
- O projeto expõe uma API HTTP pública e um endpoint WebSocket puro para ações em tempo real (salas e gameplay).
- Existem dois tipos de tokens no sistema:
  - `token`: JWT do usuário retornado por `POST /user/login` ou `POST /auth/google`
  - `tokenGameId`: JWT do jogo retornado pelos endpoints de jogo/WebSocket e usado para identificar a sala

---

## 🔐 Autenticação

- HTTP: atualmente nenhum endpoint é protegido (`SecurityConfig` permite todas as requisições).
- WebSocket: exige o parâmetro `token` na query.
- O servidor extrai o `userId` a partir do JWT antes de aceitar a conexão.
- Tempo de vida do JWT: 6 horas.
- Token inválido ou ausente → conexão rejeitada.

---

## 📦 Padrões de Resposta

### HTTP Sucesso

```
{
"success": true,
"message": "user_logged",
"data": {
"id": "uuid",
"token": "<JWT>"
}
}
```

### HTTP Erro

```
{
"success": false,
"message": "invalid_credentials"
}
```

Erros de validação:

```
{
"success": false,
"message": "email: deve ser um email válido"
}
```

---

### WebSocket Sucesso

```
{
"event": "EVENT_NAME",
"data": {}
}
```

Observação: pode conter campos como `warning`, `status`, `turnEndsAt`.

---

### WebSocket Erro

```
{
"event": "ERROR",
"message": "error_code"
}
```

---

# 🌐 Endpoints HTTP

## 👤 Usuário

### POST `/user`

Cria um usuário local.

```
{
"email": "player@example.com",
"password": "12345678"
}
```

Regras:
- email obrigatório e válido
- senha entre 8 e 16 caracteres
- nickname gerado automaticamente

---

### GET `/user/{userId}`

Busca usuário por ID.

---

### POST `/user/login`

Autentica usuário e retorna JWT.

---

### PATCH `/user/nickname/{userId}`

Define nickname do usuário.

```
{
"nickname": "Zidan"
}
```

Regras:
- obrigatório
- entre 5 e 10 caracteres
- deve ser único
- usuário não pode já ter nickname

---

### POST `/auth/google`

Autenticação via Google.

---

## 🎮 Game

### GET `/game`

Lista jogos públicos ativos.

---

### GET `/game/{tokenGameId}`

Retorna snapshot da sala.

---

### GET `/game/code/{code}`

Resolve código para `tokenGameId`.

---

# 🔌 WebSocket

## 🔑 Conexão

- URL: `ws://<host>:8080/ws/game?token=<USER_JWT>`
- Token obrigatório
- Reconexão:
  - envia `PARTICIPANT_RECONNECTED`
- Origem liberada (`*`)

---

## 📡 Formato das Mensagens

### Simples

```
{
"type": "MATCHMAKING_GAME"
}
```

### Com payload

```
{
"type": "START_GAME",
"tokenGameId": "<GAME_JWT>"
}
```

### Gameplay

```
{
"type": "PLAYER_ACTION",
"tokenGameId": "<GAME_JWT>",
"action": {
"type": "REVEAL"
}
}
```

---

# 🎮 Ações WebSocket

## CREATE_GAME

- Cria sala (`WAITING`)
- Criador vira host
- Timeout de 5 minutos

---

## JOIN_GAME

- Adiciona usuário à sala
- 2 primeiros = jogadores
- Restante = espectadores
- Limite:
  - 2 sem espectadores
  - 7 com espectadores

---

## MATCHMAKING_GAME

- Busca partida automática
- Estados:
  - `SEARCHING`
  - `FOUNDED`
- Inicia automaticamente

---

## SWAP_POSITION

- Troca posição
- Permitido antes do jogo iniciar
- Posições:
  - 0–1: jogadores
  - 2–6: espectadores

---

## START_GAME

- Apenas host
- Mínimo 2 jogadores
- Inicia timers de turno

---

## LEFT_GAME

- Remove participante
- Pode gerar `GAME_OVER`

---

## KICK_PARTICIPANT

- Apenas host
- Remove sem banir

---

## BAN_PARTICIPANT

- Remove e adiciona à blacklist

---

## UNBAN_PARTICIPANT

- Remove da blacklist

---

## DISCARD_POWER

- Remove poder do inventário

---

## PLAYER_ACTION

Tipos suportados:

- REVEAL
- BLOCK
- UNBLOCK
- TRAP
- DETECT_TRAPS
- SPY
- FREEZE
- UNFREEZE
- BLIND
- LANTERN
- IMMUNITY

---

### Resultado de ação

```
{
"event": "PLAYER_ACTION_RESULT",
"turnEndsAt": "...",
"events": [],
"data": {}
}
```

Regras:
- Apenas durante `RUNNING`
- Espectadores não jogam
- Geralmente requer turno
- Ação válida avança turno

---

# 📡 Eventos

## Principais

- GAME_CREATED
- PARTICIPANT_JOIN
- GAME_STARTED
- PLAYER_ACTION_RESULT
- GAME_OVER
- TURN_EXPIRED
- ROOM_CLOSED
- ERROR

---

## Eventos internos

- CELL_REVEALED
- WORD_FOUNDED
- CELL_BLOCKED
- TRAP_TRIGGERED
- PLAYER_FROZEN
- PLAYER_BLINDED
- TURN_PASSED
- etc.

---

# ⚠️ Observações Importantes

- `tokenGameId` é um JWT (não é ID direto)
- Salas privadas não aparecem em `/game`
- Máximo de participantes: 7
- Matchmaking usa nome padrão
---

## ⏱️ Sistema de Turnos

- Turno inicial: 45s
- Após ação: `45 + 2 * eventos`
- 3 timeouts → jogador removido

---

## 🔌 Conexão

- Desconectou → `PARTICIPANT_DISCONNECTED`
- Reconectou em até 60s → `PARTICIPANT_RECONNECTED`
- Não voltou → removido silenciosamente

---

## ⏳ Timeout de Sala

- 5 minutos sem iniciar → `ROOM_CLOSED`

---

## 🔁 Reuso de Sala

- Jogo finalizado volta para `WAITING`
- Pode ser reutilizado