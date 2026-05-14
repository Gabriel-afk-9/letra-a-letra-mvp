[CONTEXTO]
Estou desenvolvendo um jogo usando JavaScript puro (sem frameworks), já tenho parte da UI e lógica funcionando.

---

[OBJETIVO]
Implementar o fluxo completo entre matchmaking e jogo, incluindo:
1. Tela de “oponente encontrado”
2. Transição para a tela principal do jogo
3. Sistema de Player Card com troca de turno bem definida

---

[FLUXO DE TELAS]

1. MATCHMAKING → ENCONTROU OPONENTE
- Exibir uma tela intermediária quando o oponente for encontrado
- Essa tela deve mostrar:
  - Nome dos jogadores
  - Espaço para avatar (placeholder)
  - Feedback visual de conexão (ex: "Conectando..." ou "Pronto")

2. TRANSIÇÃO → JOGO
- Após alguns segundos (ou confirmação), fazer transição suave para a tela do jogo
- Evitar troca brusca de tela

---

[PLAYER CARD - COMPORTAMENTO CORRETO]

A troca de turno DEVE:

❌ NÃO:
- Sobrepor um card em cima do outro
- Ficar alternando visualmente como “camadas”

✅ SIM:
- Um card substitui o outro completamente
- A transição deve dar sensação de continuidade

---

[ANIMAÇÃO DE TROCA DE TURNO (ESSENCIAL)]

Implementar uma animação limpa e previsível:

- Card atual:
  - Fade out + leve deslocamento lateral

- Após isso:
  - Atualizar conteúdo (nome, cor, poderes, etc.)

- Novo estado:
  - Entra com fade in + slide oposto

IMPORTANTE:
- Nunca manter dois cards visíveis ao mesmo tempo
- A troca deve parecer uma substituição, não um overlay

---

[MOBILE FIRST (OBRIGATÓRIO)]

- Desenvolver pensando primeiro em telas pequenas
- Layout adaptado para mobile:
  - Elementos empilhados verticalmente
  - Espaçamentos consistentes
  - Tamanhos tocáveis (botões, cards)

- Depois adaptar para telas maiores (responsivo)

---

[REQUISITOS TÉCNICOS]

- JavaScript puro (sem frameworks)
- Separar:
  - Lógica de estado
  - Renderização
  - Animação

- Evitar manipulação caótica de DOM
- Usar classes CSS para animações (em vez de tudo via JS)

---

[ENTREGA ESPERADA]

- Estrutura das telas (matchmaking → found → game)
- Implementação do Player Card
- Lógica de troca de turno
- Animações (CSS + JS)
- Explicação breve das decisões

---

[OBS]
Quero um MVP funcional, fluido e jogável.
Evitar soluções complexas ou desnecessárias.