# Eco

Eco e um PWA de controle financeiro pessoal, construido como projeto de portfolio full stack com Java/Spring Boot, PostgreSQL e Next.js.

O MVP permite registrar e acompanhar receitas, despesas, transferencias, cartao, orcamentos, metas e dashboard mensal. O foco do projeto e demonstrar organizacao de backend, API REST, autenticacao JWT, regras de negocio financeiras, testes e integracao com frontend responsivo.

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

- Login com JWT, refresh token e logout.
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

### Backend

```powershell
cd backend
docker compose up -d
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

O banco local cria um usuario de desenvolvimento via migration para facilitar testes do fluxo completo de autenticacao.

## Validacao

Backend:

```powershell
cd backend
$env:JAVA_HOME='C:\Program Files\Java\jdk-21.0.11'
.\mvnw.cmd test
```

Frontend:

```powershell
cd frontend
npm run build
```

Ultima validacao local:

- Backend: 65 testes passando.
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

## Observacoes

Este repositorio e uma vitrine de portfolio para execucao local. Para adaptar o projeto para outro ambiente, revise o seed local, configure variaveis de ambiente proprias e troque o segredo JWT.

## Documentos

- [Escopo do MVP](./docs/MVP_SCOPE.md)
- [Backlog](./docs/BACKLOG.md)
- [Contrato da API](./docs/API_CONTRACT.md)
- [Notas de estudo backend](./docs/JR_BACKEND_NOTES.md)
- [Seguranca](./SECURITY.md)
- [Contribuicao](./CONTRIBUTING.md)
