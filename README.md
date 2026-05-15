# Eco

Eco e um PWA de controle financeiro pessoal, construido como projeto de portfolio full stack com Java/Spring Boot, PostgreSQL e Next.js.

O MVP permite registrar e acompanhar receitas, despesas, transferencias, cartao, orcamentos, metas e dashboard mensal. O foco do projeto e demonstrar organizacao de backend, API REST, autenticacao JWT com cookies HttpOnly, regras de negocio financeiras, testes e integracao com frontend responsivo.

## Stack

### Backend

- Java 21
- Spring Boot 3.5
- Spring Web, Spring Security e JWT
- Spring Data JPA / Hibernate
- PostgreSQL + Flyway
- Bean Validation
- Springdoc OpenAPI / Swagger
- JUnit, Mockito e AssertJ
- GitHub Actions CI

### Frontend

- Next.js 16
- React 19
- TypeScript
- CSS puro
- PWA mobile-first
- Recharts
- Framer Motion
- Lucide React

## Funcionalidades Do MVP

- Login com JWT, refresh token, cookies HttpOnly e logout.
- Isolamento de dados por usuario autenticado.
- CRUD de contas, categorias e transacoes.
- Filtros e paginacao de transacoes.
- Transferencias entre contas.
- Compras parceladas.
- Despesas de cartao por `billingMonth`.
- Orcamento mensal geral e por categoria.
- Metas financeiras com progresso manual.
- Dashboard mensal com resumo, categorias, fluxo de caixa, orcamento e metas.
- Fallback mockado no frontend quando o backend esta desligado.
- PWA base com layout responsivo e dark mode.

## Estrutura

```text
Eco/
  backend/    Spring Boot API
  frontend/   Next.js PWA
  docs/       escopo, backlog e contrato da API
```

## Rodar Localmente

Pre-requisitos:

- JDK 21 com `JAVA_HOME` apontando para o JDK.
- Docker Desktop ativo.
- Node.js LTS.

### Backend

```powershell
cd backend
docker compose up -d
$env:SPRING_PROFILES_ACTIVE='dev'
.\mvnw.cmd spring-boot:run
```

API:

```text
http://localhost:8080/api
```

Swagger:

```text
http://localhost:8080/swagger-ui/index.html
```

### Frontend

```powershell
cd frontend
npm install
npm run dev
```

App:

```text
http://localhost:3000
```

Se precisar trocar a URL da API:

```text
NEXT_PUBLIC_API_BASE_URL=http://localhost:8080/api
```

## Usuario Local De Desenvolvimento

O profile `dev` aplica migrations extras em `backend/src/main/resources/db/dev-migration`
para criar um usuario local e facilitar testes do fluxo completo de autenticacao.

Esse seed nao roda no profile padrao. Fora do profile `dev`, as variaveis sensiveis
sao obrigatorias e a aplicacao falha ao iniciar se `ECO_JWT_SECRET` ou credenciais
do banco nao forem informadas.

## Validacao

Backend:

```powershell
cd backend
$env:JAVA_HOME='C:\Program Files\Java\jdk-21.0.11'
$env:SPRING_PROFILES_ACTIVE='dev'
$env:ECO_JWT_SECRET='local-test-secret-change-before-open-source-123456'
$env:ECO_COOKIE_SECURE='false'
.\mvnw.cmd test
```

Frontend:

```powershell
cd frontend
npm audit
npm run build
```

Ultima validacao local:

- Backend: 66 testes passando.
- Frontend: `npm audit` com 0 vulnerabilidades.
- Frontend: build Next.js passando.

## API

Principais grupos de endpoints:

- `/api/auth`
- `/api/accounts`
- `/api/categories`
- `/api/transactions`
- `/api/budgets`
- `/api/goals`
- `/api/dashboard`
- `/api/reports`

Contrato detalhado: [docs/API_CONTRACT.md](./docs/API_CONTRACT.md)

## Seguranca E Open Source

Este repositorio esta preparado para ficar publico como projeto de portfolio.
Os defaults perigosos foram removidos do profile padrao:

- `ECO_JWT_SECRET` e obrigatorio fora do profile `dev`.
- Credenciais do banco sao obrigatorias fora do profile `dev`.
- O Postgres local fica bindado em `127.0.0.1`.
- O usuario `dev@eco.com` fica isolado em migrations de desenvolvimento.
- Tokens de autenticacao usam cookies HttpOnly, nao `localStorage`.

Para deploy publico real, rode sem profile `dev`, use HTTPS, mantenha
`ECO_COOKIE_SECURE=true`, configure CORS para o dominio final e use segredos fortes.

## Documentos

- [Escopo do MVP](./docs/MVP_SCOPE.md)
- [Backlog](./docs/BACKLOG.md)
- [Contrato da API](./docs/API_CONTRACT.md)
- [Seguranca](./SECURITY.md)
- [Contribuicao](./CONTRIBUTING.md)
