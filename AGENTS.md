# Eco — Contexto para Agentes

Este documento consolida tudo o que um agente de código precisa saber para trabalhar no projeto Eco sem depender de conversas anteriores.

## Visão Geral

Eco é um PWA pessoal de controle financeiro, com backend Java/Spring Boot e frontend Next.js. Nasceu para resolver uma necessidade real — acompanhar gastos, receitas, cartão, orçamentos e metas — e funciona como projeto de portfólio e trilha de estudo full stack.

O foco é começar simples, funcional e evoluir por fases pequenas e bem testadas. Complexidade prematura é evitada conscientemente.

## Stack

### Backend
- Java 21
- Spring Boot 3.5.14
- Spring Web, Spring Data JPA, Spring Security (temporariamente `permitAll`)
- Bean Validation, Springdoc OpenAPI
- PostgreSQL + Flyway
- JUnit, Mockito, AssertJ
- GitHub Actions CI

### Frontend
- Next.js 16 (App Router)
- React 19, TypeScript
- CSS puro (sem biblioteca de UI components)
- Recharts (gráficos)
- Framer Motion (animações)
- Lucide React (ícones)
- PWA mobile-first, responsivo para desktop
- Dark mode suportado

### Infraestrutura
- Docker Compose para ambiente local
- Deploy futuro em plataforma como Render, Railway, Fly.io ou Vercel

## Estrutura do Repositório

```text
Eco/
  AGENTS.md               <- este arquivo
  README.md               <- visão geral para humanos
  backend/                <- Spring Boot
    src/main/java/com/eco/
      account/
      category/
      transaction/
      report/
      common/exception/
      config/
    src/main/resources/
      db/migration/         <- Flyway migrations
      application.yaml
    docker-compose.yml
    mvnw.cmd
  docs/
    MVP_SCOPE.md            <- escopo técnico completo do MVP
    BACKLOG.md              <- backlog técnico com checkboxes
    API_CONTRACT.md         <- contrato REST com exemplos
    FRONTEND_HANDOFF_KIMI.md <- handoff para integração frontend
    JR_BACKEND_NOTES.md     <- guia de estudo para dev Jr
  frontend/                 <- Next.js
    src/
      app/                  <- páginas (App Router)
      components/           <- componentes reutilizáveis
      lib/                  <- utilitários, API client
      mocks/                <- dados mockados para fallback
    public/
    package.json
```

## Decisões Arquiteturais

### Backend
- **Monolito modular**: código organizado por domínio (`account/`, `category/`, `transaction/`), mas roda como aplicação única
- **Controllers finos, services com regras de negócio**
- **DTOs como contrato externo**: nunca expor entities diretamente
- **Soft delete**: `active=false` (contas, categorias, transações); `deleted_at` para transações futuras
- **BigDecimal para dinheiro**: nunca `double` ou `float`
- **UUID como chave primária**
- **Flyway versiona o schema**: `ddl-auto: validate` no Hibernate
- **Auditoria**: `created_at`, `updated_at` em UTC (`Instant`)
- **Erros padronizados**: `@RestControllerAdvice` com `ErrorResponse`

### Frontend
- **CSS puro com design tokens**: variáveis CSS para cores, espaçamentos, tipografia, suportando dark mode
- **Mobile-first**: layout funciona em telas pequenas antes de desktop
- **Fallback mockado**: quando backend está desligado, o app usa mocks e mostra badge "modo demonstração"
- **Client HTTP simples**: `fetch` com wrapper em `lib/api.ts`
- **PWA**: manifest, service worker (futuro), viewport configurado
- **Animações**: Framer Motion para micro-interações e transições de layout
- **Gráficos**: Recharts para dataviz no dashboard

## Regras Financeiras Críticas
- `amount` sempre positivo
- Tipo da transação (`INCOME`/`EXPENSE`) define o sentido financeiro
- Transações usam `LocalDate` (`occurredAt`)
- Despesas de cartão usam `billingMonth`
- Dashboard e orçamento consideram cartão pelo `billingMonth`
- Orçamento considera apenas despesas
- Transferência é uma transação única com `account_id` (origem) e `transfer_account_id` (destino)

## API — Endpoints Atuais

Base: `http://localhost:8080/api`

CORS liberado para `http://localhost:3000`.

### Implementados
- `GET /categories` — listar
- `POST /categories` — criar
- `GET /categories/{id}` — buscar
- `PUT /categories/{id}` — editar
- `DELETE /categories/{id}` — arquivar
- `GET /accounts` — listar
- `POST /accounts` — criar
- `GET /accounts/{id}` — buscar
- `PUT /accounts/{id}` — editar
- `DELETE /accounts/{id}` — arquivar
- `GET /transactions` — listar com filtros e paginação
- `POST /transactions` — criar receita/despesa
- `PUT /transactions/{id}` — editar
- `DELETE /transactions/{id}` — soft delete
- `GET /reports/monthly-summary?year=&month=` — resumo mensal

### Planejados (não implementados)
- Auth: `POST /auth/login`, `POST /auth/refresh`, `POST /auth/logout`, `GET /auth/me`
- Transferências: `POST /transactions/transfers`
- Parcelamento: `POST /transactions/installments`
- Cartão: `GET /transactions/card-summary?billingMonth=`
- Budgets: `GET /budgets/{month}`, `PUT /budgets/{month}`, etc.
- Goals: `GET /goals`, `POST /goals`, etc.
- Dashboard completo: `GET /dashboard/monthly`, `GET /dashboard/categories`, `GET /dashboard/cash-flow`

Detalhes completos em `docs/API_CONTRACT.md`.

## Checklist de Qualidade

Antes de considerar uma tarefa completa:

- [ ] `npm run build` passa no frontend
- [ ] `mvnw.cmd test` passa no backend
- [ ] Layout responsivo funciona em mobile e desktop
- [ ] Acessibilidade: labels, foco visível, aria-labels em icon-only
- [ ] Fallback mockado funciona quando backend está desligado
- [ ] Dark mode renderiza corretamente
- [ ] Código segue o estilo existente do projeto

## Convenções de Código

### Java
- Package base: `com.eco`
- Nomes em inglês, comentários e mensagens em português
- Services testados com JUnit + Mockito
- Controllers testados com `@WebMvcTest` para fluxos críticos

### TypeScript / React
- Componentes como funções com tipos explícitos
- Props interfaces com nome `XxxProps`
- Hooks customizados em `lib/hooks.ts` se necessário
- CSS modules ou classes globais em `globals.css`
- `use client` apenas quando necessário (interatividade, hooks)

## Como Integrar Frontend e Backend

1. Backend precisa estar rodando: `docker compose up -d` + `mvnw.cmd spring-boot:run`
2. Frontend aponta para `http://localhost:8080/api` (ou `NEXT_PUBLIC_API_BASE_URL`)
3. `frontend/src/lib/api.ts` contém todas as funções de HTTP
4. Se backend retornar erro de rede (fetch falha), dashboard cai para mocks
5. Novos endpoints devem ser adicionados em `lib/api.ts` antes de usados nas páginas

## O que a IA Não Deve Fazer Sem Confirmação

- Implementar entidades ou regras de negócio backend por conta própria
- Mudar contrato da API sem atualizar `API_CONTRACT.md`
- Adicionar bibliotecas de UI components (MUI, Chakra, etc.)
- Remover fallback mockado
- Alterar stack principal (Next.js -> outro framework, Java -> outra linguagem)
- Fazer `git push` sem confirmação explícita

## Roadmap Imediato (MVP)

1. **Auth/JWT** — backend: entidades `User`, `RefreshToken`; frontend: tela `/login`
2. **Transferências** — endpoint `POST /transactions/transfers`
3. **Parcelamento** — endpoint `POST /transactions/installments`
4. **Budgets e Goals** — CRUD + telas
5. **Dashboard completo** — endpoints agregados com gráficos
6. **Deploy** — escolher plataforma e publicar

## Roadmap Futuro (Pós-MVP)

- Importação CSV/XLSX/PDF
- Sugestão de categoria por IA
- Chat financeiro com dados do PostgreSQL
- Open Finance
- Notificações, recorrências, tags, anexos

## Contato e Contexto

- Projeto pessoal de portfólio
- Trilha de estudo para vaga Jr backend Java e full stack
- IA apoia revisão, testes, organização e explicações — não substitui decisões arquiteturais
- O banco de dados é a fonte da verdade; IA sugere, usuário confirma

---

Última atualização: 2026-05-10
