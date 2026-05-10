# Eco Frontend

Frontend em Next.js ainda mockado.

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

## Estado Atual

Este frontend usa dados mockados em `src/mocks/finance-data.ts`.

O backend Spring Boot ja possui endpoints suficientes para a primeira integracao:

- `GET http://localhost:8080/api/reports/monthly-summary?year=2026&month=5`
- `GET http://localhost:8080/api/transactions?page=0&size=10`
- `GET http://localhost:8080/api/categories`
- `GET http://localhost:8080/api/accounts`

A integracao do frontend sera feita com outro modelo de IA via opencode/Kimi. Use `../docs/API_CONTRACT.md` e `../docs/FRONTEND_HANDOFF_KIMI.md` como fonte do contrato atual.

O backend ja permite CORS para:

```text
http://localhost:3000
```

## Proxima Integracao Recomendada

1. Criar client HTTP em `src/lib/api.ts`.
2. Configurar base URL `http://localhost:8080/api`.
3. Trocar o card de resumo mensal para consumir `/reports/monthly-summary`.
4. Trocar lista de transacoes para consumir `/transactions?page=0&size=10`.
5. Tratar loading/erro simples.
