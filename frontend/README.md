# Eco Frontend

Frontend mockado em Next.js para validar a experiencia do PWA antes do backend estar pronto.

## Objetivo

- Mostrar o produto visualmente.
- Testar fluxo mobile-first.
- Servir como apoio de portfolio.
- Permitir que o backend seja implementado manualmente em Java sem bloquear a interface.

## Rodar Localmente

Instalar dependencias:

```bash
npm install
```

Rodar:

```bash
npm run dev
```

Abrir:

```text
http://localhost:3000
```

## Importante

Este frontend usa dados mockados em `src/mocks/finance-data.ts`.

Quando a API Spring Boot estiver pronta, trocar os mocks por chamadas HTTP seguindo `../docs/API_CONTRACT.md`.

