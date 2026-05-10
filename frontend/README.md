# Eco Frontend

Frontend em Next.js do Eco, um PWA pessoal de controle financeiro.

## Stack

- Next.js 16
- React 19
- TypeScript
- CSS puro
- Lucide React

## Rodar Localmente

Instalar dependencias:

```powershell
npm install
```

Rodar em desenvolvimento:

```powershell
npm run dev
```

Abrir:

```text
http://localhost:3000
```

Por padrao, o frontend usa:

```text
http://localhost:8080/api
```

Para alterar, crie `.env.local`:

```text
NEXT_PUBLIC_API_BASE_URL=http://localhost:8080/api
```

## Backend Necessario

Antes de usar os fluxos reais:

```powershell
cd ..\backend
docker compose up -d
.\mvnw.cmd spring-boot:run
```

## Funcionalidades Atuais

- Dashboard integrado ao backend.
- Resumo mensal via `GET /reports/monthly-summary`.
- Ultimas transacoes via `GET /transactions`.
- Criacao de transacao via `POST /transactions`.
- Tela `/transactions` com filtros por periodo, tipo, conta e categoria.
- Edicao via `PUT /transactions/{id}`.
- Exclusao via `DELETE /transactions/{id}`.
- Fallback para mocks quando a API esta indisponivel no dashboard.
- PWA base com manifest, tema mobile e icone.

## Estrutura Relevante

```text
src/app/page.tsx
src/app/transactions/page.tsx
src/components/TransactionModal.tsx
src/lib/api.ts
src/lib/format.ts
src/mocks/finance-data.ts
```

## Scripts

Build de producao:

```powershell
npm run build
```

Rodar producao local apos build:

```powershell
npm run start
```

## Pendencias Frontend

- Tela de login quando auth/JWT existir no backend.
- Client HTTP autenticado com Bearer token e refresh token.
- Telas dedicadas de contas e categorias.
- Telas de orcamentos e metas quando os endpoints existirem.
- Service worker/offline cache mais completo.
- Refinos visuais para portfolio, screenshots e responsividade final.
