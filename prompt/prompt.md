[CONTEXTO]
Estou desenvolvendo um jogo usando JavaScript puro (sem frameworks). 
A estrutura do projeto está organizada em:
- components (board, cell, inventory, player, words)
- pages (Game, Home, Matchmaking)
- services (authService, gameService)
- websocket (socket.js)
- state (store.js)

O projeto já está parcialmente funcional e integrado com uma API.

---

[PROBLEMA]
O jogo está muito bugado. Exemplo:
- Ao clicar em uma célula, outra célula é revelada
- Comportamentos inconsistentes no estado do jogo
- Possível problema de sincronização entre UI e estado (store)

---

[OBJETIVO]
Quero transformar isso em um MVP funcional e jogável, com:
- Código limpo (Clean Code)
- Organização clara de responsabilidades
- Separação correta entre estado, UI e lógica
- Uso correto de todas as funções disponíveis da API

---

[INSTRUÇÕES]
1. Analise os códigos que vou enviar (principalmente o arquivo .md com exemplos da API)
2. Use esses exemplos como base oficial de comportamento esperado
3. Identifique possíveis causas dos bugs atuais
4. Refatore o código para:
   - Corrigir os bugs
   - Melhorar a arquitetura
   - Garantir previsibilidade do estado
5. Sugira melhorias estruturais (se necessário), sem complicar demais (é um MVP)

---

[REGRAS IMPORTANTES]
- Não usar frameworks (React, Vue, etc.)
- Manter JavaScript puro
- Evitar overengineering
- Priorizar simplicidade e funcionamento correto
- Explicar brevemente as decisões mais importantes

---

[CÓDIGOS]
(Aqui vou enviar os arquivos, começando pelo .md com exemplos da API)

---------------------------------------------

[CONTEXTO]
Estou desenvolvendo um jogo usando JavaScript puro (sem frameworks), com arquitetura baseada em:
- components (board, cell, inventory, player, words)
- state global (store.js)
- integração com API já funcional

A base do jogo já foi estabilizada:
- Os cliques no tabuleiro estão funcionando corretamente
- O estado está consistente

---

[OBJETIVO]
Agora quero evoluir para um MVP jogável adicionando o sistema visual de:
- Palavras (words)
- Cartas/Poderes (inventory)

Esses elementos devem reagir dinamicamente ao estado global (store).

---

[LAYOUT DESEJADO]
A tela do jogo deve seguir EXATAMENTE essa hierarquia vertical:

1. Player Card (informações do jogador)
2. Words (palavras disponíveis/jogadas)
3. Board (tabuleiro do jogo)
4. Inventory (cartas/poderes)

(Ordem de cima para baixo)

---

[INSTRUÇÕES]
1. Criar/estruturar os componentes:
   - words.js
   - inventory.js

2. Integrar ambos com o store global:
   - Renderização baseada no estado
   - Atualização automática ao mudar o estado

3. Garantir:
   - Separação clara entre lógica e UI
   - Componentes reutilizáveis
   - Código limpo (Clean Code)

4. O sistema deve permitir:
   - Exibir palavras ativas
   - Exibir cartas/poderes disponíveis
   - Reagir a mudanças do modo NORMAL (vindas da API/store)

---

[REGRAS]
- Usar apenas JavaScript puro
- Não usar frameworks
- Evitar complexidade desnecessária (foco em MVP)
- Não quebrar a estrutura atual do projeto

---

[RESULTADO ESPERADO]
- Estrutura clara dos componentes words.js e inventory.js
- Exemplo de renderização
- Integração com o store (subscribe, update, etc.)
- Explicação breve das decisões importantes


--------------------------------

[CONTEXTO]
Estou desenvolvendo um jogo usando JavaScript puro (sem frameworks). 
Já tenho parte da interface funcionando e agora preciso criar/refatorar o componente de Player Card.

---

[OBJETIVO]
Criar um componente de "Player Card" reutilizável, representando o jogador e o oponente, com foco em:
- Clareza visual
- Feedback de turno (UX)
- Animações suaves
- Código limpo e organizado

---

[REQUISITOS DO CARD]

1. IDENTIDADE VISUAL
- Meu card (player local): cor LARANJA
- Card do oponente: cor AZUL
- Deve ser fácil diferenciar visualmente quem é quem

2. CONTEÚDO
O card deve conter:
- Espaço para foto do jogador (apenas placeholder por enquanto)
- Nome do jogador
- Poder do jogador representado por bolinhas:
  - Bolinha branca preenchida = possui poder
  - Bolinha apagada/vazia = poder usado ou indisponível

---

[COMPORTAMENTO - TURNO (ESSENCIAL)]

Quando o turno mudar:

O card deve atualizar dinamicamente com animação suave:

Opção base (obrigatória):
- Fade + slide:
  - Card atual perde opacidade e se move levemente para o lado
  - Dados são atualizados
  - Novo estado entra com animação suave

Melhoria desejada:
- Transição de cor animada (lerp entre laranja ↔ azul)
- Bolinhas de poder atualizando com animação (ex: fade ou scale)
- Pequeno efeito de "pop" ao atualizar o card

---

[DESTAQUE DE TURNO (UX)]

Além da cor, reforçar de quem é o turno:

- Card ativo deve ter:
  - Glow leve (outline ou sombra)
  - Destaque visual claro

- Texto dinâmico:
  - "Sua vez" (quando for o player)
  - "Vez do oponente"

---

[REQUISITOS TÉCNICOS]

- Usar apenas JavaScript puro (sem frameworks)
- Separar responsabilidades:
  - Renderização (DOM)
  - Estado (dados do player)
  - Atualização de UI
- Código organizado e legível (Clean Code)
- Evitar manipulação confusa de DOM
- Pensar em reutilização do componente

---

[ENTREGA ESPERADA]

- Estrutura do componente (JS)
- Exemplo de renderização no DOM
- Função para atualizar o estado (ex: updatePlayerCard)
- Implementação das animações (CSS + JS se necessário)
- Explicação breve das decisões importantes

---

[OBS]
Priorizar um MVP funcional e bonito, sem overengineering.