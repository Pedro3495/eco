# Eco - Escopo Tecnico Inicial do MVP

## Objetivo

Construir um PWA pessoal de controle financeiro, com backend em Java/Spring Boot e PostgreSQL, focado primeiro em uso diario manual: registrar receitas, despesas, transferencias, acompanhar cartao, orcamentos mensais e metas.

IA, importacao de arquivos e investimentos detalhados ficam preparados na arquitetura, mas nao entram como funcionalidades ativas do MVP.

O projeto tambem serve como portfolio e trilha pratica de estudo em Java/Spring Boot. Por isso, as decisoes devem favorecer clareza, boas praticas, testes e organizacao progressiva, sem inflar o MVP com complexidade desnecessaria.

## Status Atual Do Backend

Ja implementado:

- Java 21 + Spring Boot 3.5.14.
- PostgreSQL + Flyway.
- Migrations para `categories`, `accounts` e `transactions`.
- Migrations para `users` e `refresh_tokens`.
- Seed local do usuario `dev@eco.com`.
- Auth JWT com `POST /auth/login`, `POST /auth/refresh`, `POST /auth/logout` e `GET /auth/me`.
- Filtro JWT conectado ao Spring Security.
- Endpoints privados protegidos por Bearer token.
- CRUD de categorias.
- CRUD de contas.
- CRUD de transacoes de receita/despesa.
- Soft delete via `active=false`.
- Filtros em transacoes por conta, categoria, tipo, periodo e ativo.
- Paginacao em `GET /api/transactions`.
- Regra de categoria compativel com tipo da transacao.
- Resumo mensal simples em `GET /api/reports/monthly-summary`.
- Tratamento global de erros.
- Testes unitarios de services.
- Testes de controller para reports, auth e seguranca.
- Teste unitario do filtro JWT.

Ainda nao implementado:

- Escopo por usuario em contas, categorias e transacoes.
- Transferencias.
- Cartao/fatura/billingMonth.
- Parcelamento.
- Budgets.
- Goals.
- Dashboard completo.
- Refresh token automatico no frontend.

## Decisoes Do MVP

- Uso inicial: pessoal, para um unico usuario real.
- Autenticacao: login com JWT, access token curto e refresh token.
- Modelo tecnico: dados sempre associados a `User`, mesmo que exista apenas um usuario.
- Movimentacoes: receitas, despesas e transferencias internas.
- Transferencias: uma transacao unica com conta de origem e destino.
- Contas: uma conta bancaria principal e um cartao de credito separado.
- Saldo inicial: campo `initial_balance` na conta.
- Cartao: transacoes com `billingMonth`, sem fatura formal no MVP.
- Pagamento de fatura: sem modelagem formal no MVP.
- Dashboard mensal: despesas de cartao entram pelo `billingMonth`; demais transacoes entram por `transactionDate`.
- Parcelamento: simples, gerando transacoes futuras ligadas por `installmentGroupId`.
- Categorias: editaveis, com seed inicial.
- Categorias: sem subcategorias no MVP.
- Exclusao: soft delete para dados financeiros.
- Auditoria: campos basicos de criacao, atualizacao e exclusao; historico completo fica para depois.
- Orcamentos: por categoria, com limite geral mensal opcional.
- Orcamentos controlam despesas; receitas ficam no dashboard/fluxo de caixa.
- Metas: simples, por valor alvo.
- Metas: independentes de contas/investimentos no MVP.
- IA: nao ativa no MVP, mas modelo preparado para sugestoes futuras.
- Importacao: nao ativa no MVP, mas transacoes preparadas com campos de origem.
- Frontend: Next.js mobile-first PWA responsivo para desktop.
- Ambiente inicial: local com Docker Compose; deploy fica para depois.
- Arquitetura backend: monolito modular em Spring Boot.

## Funcionalidades Do MVP

### Autenticacao

- Login com email e senha.
- Geracao de access token e refresh token.
- Renovacao de sessao.
- Logout com revogacao de refresh token.
- Protecao dos endpoints privados.
- Criacao manual ou seed de um usuario inicial.

### Contas

- Criar, editar, listar e arquivar contas.
- Tipos iniciais:
  - `CHECKING`
  - `CASH`
  - `CREDIT_CARD`
  - `INVESTMENT`
- Para o MVP, usar pelo menos:
  - conta bancaria principal;
  - cartao de credito.

### Categorias

- CRUD de categorias.
- Categorias padrao no primeiro uso:
  - Alimentacao
  - Moradia
  - Transporte
  - Saude
  - Educacao
  - Lazer
  - Assinaturas
  - Compras
  - Impostos
  - Receita
  - Investimentos
  - Outros
- Categoria pode ter tipo sugerido: `INCOME`, `EXPENSE` ou `BOTH`.
- Subcategorias ficam fora do MVP.

### Transacoes

- Criar receita.
- Criar despesa.
- Criar transferencia entre contas.
- Listar por periodo.
- Filtrar por conta, categoria, tipo e texto.
- Buscar por texto em descricao, estabelecimento e descricao bruta.
- Editar transacao.
- Excluir ou arquivar transacao.
- Suportar parcelamento simples.

### Cartao De Credito

- Registrar despesa no cartao.
- Informar mes de fatura por `billingMonth`.
- Ver total do cartao por mes de fatura.
- Sem entidade formal de fatura no MVP.

### Orcamentos

- Definir orcamento mensal por categoria.
- Definir limite geral mensal opcional.
- Comparar gasto realizado vs planejado.
- Considerar apenas despesas no calculo de consumo do orcamento.

### Metas

- Criar meta com nome, valor alvo, valor atual e data alvo opcional.
- Atualizar progresso manualmente.
- Marcar meta como ativa, concluida ou arquivada.
- Nao vincular metas a contas ou investimentos no MVP.

### Dashboard

- Saldo geral por periodo.
- Total de receitas.
- Total de despesas.
- Resultado mensal.
- Gastos por categoria.
- Orcamento consumido.
- Progresso das metas.
- Total do cartao por `billingMonth`.

## Fora Do MVP

- IA ativa.
- Chat financeiro.
- Categorizacao automatica.
- Importacao CSV/XLSX/PDF.
- Open Finance.
- Fatura completa com fechamento, vencimento e pagamento.
- Pagamento formal de fatura de cartao.
- Investimentos detalhados.
- Cotacoes.
- Multiusuario real.
- Compartilhamento familiar.
- Relatorios avancados.
- Exportacao CSV/PDF.
- Historico completo de alteracoes.
- Anexos e comprovantes.
- Notificacoes e lembretes.
- Lancamentos recorrentes.
- Tags em transacoes.

## Arquitetura Backend

Usar monolito modular em Spring Boot. O sistema roda como uma unica aplicacao, mas o codigo fica organizado por modulos de negocio.

Evitar multiplos servicos no MVP. A separacao em microservicos nao traz beneficio neste escopo e aumentaria deploy, observabilidade, seguranca e transacoes distribuidas sem necessidade.

Dentro de cada modulo, usar estrutura tradicional do Spring:

```text
controller/
service/
repository/
dto/
model/
```

Manter controllers finos, regras de negocio em services, persistencia em repositories e contratos externos em DTOs.

Usar Bean Validation nos DTOs de entrada:

- `@NotNull` para campos obrigatorios.
- `@Positive` para valores monetarios.
- `@Size` para textos.
- Validacoes condicionais em service quando dependerem do estado do banco, como `billingMonth` obrigatorio para cartao.

Testes backend no MVP:

- Services para regras de negocio.
- Repositories para queries importantes.
- Controllers apenas para fluxos criticos.
- Prioridade: transacoes, parcelamento, transferencia, orcamento, autenticacao e filtros principais.

Documentacao de API:

- Usar Springdoc OpenAPI.
- Expor Swagger UI em ambiente local.
- Manter DTOs e exemplos basicos para facilitar desenvolvimento do frontend.

## Modulos Do Backend

Estrutura sugerida:

```text
src/main/java/com/eco
  auth/
  user/
  account/
  category/
  transaction/
  budget/
  goal/
  dashboard/
  common/
  config/
```

### `auth`

- Login.
- JWT.
- Filtros Spring Security.
- DTOs de autenticacao.

### `user`

- Entidade `User`.
- Repositorio.
- Seed ou criacao inicial.

### `account`

- Entidade `Account`.
- CRUD de contas.
- Calculo de saldo.

### `category`

- Entidade `Category`.
- CRUD e seed de categorias.

### `transaction`

- Entidade `Transaction`.
- Criacao, edicao, listagem e filtros.
- Logica de transferencia.
- Logica de parcelamento simples.

### `budget`

- Entidades `MonthlyBudget` e `CategoryBudget`.
- Consultas de realizado vs planejado.

### `goal`

- Entidade `Goal`.
- CRUD de metas.

### `dashboard`

- Queries agregadas.
- DTOs de resumo mensal.
- Sem regra de negocio pesada.

### `common`

- Excecoes.
- Respostas padrao.
- Validadores.
- Tipos compartilhados.
- Auditoria simples.

## Padrao De Erros Da API

Usar `@RestControllerAdvice` para padronizar erros.

Formato:

```json
{
  "timestamp": "2026-05-06T10:00:00Z",
  "status": 400,
  "error": "VALIDATION_ERROR",
  "message": "Dados invalidos",
  "fields": {
    "amount": "deve ser maior que zero"
  }
}
```

Tipos iniciais:

- `VALIDATION_ERROR`
- `AUTHENTICATION_ERROR`
- `AUTHORIZATION_ERROR`
- `NOT_FOUND`
- `BUSINESS_RULE_ERROR`
- `INTERNAL_ERROR`

## Entidades Principais

### User

```text
id UUID PK
name varchar
email varchar unique
password_hash varchar
created_at timestamp
updated_at timestamp
```

### RefreshToken

```text
id UUID PK
user_id UUID FK -> users.id
token_hash varchar
expires_at timestamp
revoked_at timestamp nullable
created_at timestamp
```

### Account

```text
id UUID PK
user_id UUID FK -> users.id
name varchar
type varchar -- CHECKING, CASH, CREDIT_CARD, INVESTMENT
currency varchar default 'BRL'
active boolean
initial_balance numeric(14,2)
created_at timestamp
updated_at timestamp
```

Saldo deve ser calculado por `initial_balance` mais transacoes.

### Category

```text
id UUID PK
user_id UUID FK -> users.id
name varchar
kind varchar -- INCOME, EXPENSE, BOTH
color varchar nullable
icon varchar nullable
active boolean
created_at timestamp
updated_at timestamp
```

### Transaction

```text
id UUID PK
user_id UUID FK -> users.id
account_id UUID FK -> accounts.id
transfer_account_id UUID FK -> accounts.id nullable
category_id UUID FK -> categories.id nullable
type varchar -- INCOME, EXPENSE, TRANSFER
amount numeric(14,2)
transaction_date date
billing_month char(7) nullable -- YYYY-MM, usado para cartao
description varchar
merchant_name varchar nullable
raw_description text nullable
notes text nullable
source varchar -- MANUAL, CSV_IMPORT, XLSX_IMPORT, PDF_IMPORT, OPEN_FINANCE
external_id varchar nullable
import_batch_id UUID nullable
installment_group_id UUID nullable
installment_number int nullable
installment_total int nullable
category_origin varchar -- MANUAL, AI_SUGGESTED, IMPORTED_RULE, UNKNOWN
deleted_at timestamp nullable
created_at timestamp
updated_at timestamp
```

Regras:

- `amount` sempre positivo.
- `type` define o sentido financeiro.
- Transferencia sera registrada como uma transacao unica, com `account_id` como origem e `transfer_account_id` como destino.
- Despesa de cartao usa `account_id` apontando para conta do tipo `CREDIT_CARD`.
- `billing_month` deve existir para despesas de cartao.
- Dashboard e orcamento devem considerar despesa de cartao pelo `billing_month`, nao pela data da compra.
- Exclusao de transacao deve preencher `deleted_at`, nao remover a linha fisicamente.

### MonthlyBudget

```text
id UUID PK
user_id UUID FK -> users.id
month char(7) -- YYYY-MM
general_limit numeric(14,2) nullable
created_at timestamp
updated_at timestamp
```

### CategoryBudget

```text
id UUID PK
monthly_budget_id UUID FK -> monthly_budgets.id
category_id UUID FK -> categories.id
limit_amount numeric(14,2)
created_at timestamp
updated_at timestamp
```

Constraint recomendada:

```text
unique(monthly_budget_id, category_id)
```

### Goal

```text
id UUID PK
user_id UUID FK -> users.id
name varchar
target_amount numeric(14,2)
current_amount numeric(14,2)
target_date date nullable
status varchar -- ACTIVE, COMPLETED, ARCHIVED
created_at timestamp
updated_at timestamp
```

## Endpoints REST Iniciais

Prefixo: `/api`

### Auth

```text
POST /auth/login
POST /auth/refresh
POST /auth/logout
GET /auth/me
```

Opcional no desenvolvimento:

```text
POST /auth/register
```

### Accounts

```text
GET /accounts
POST /accounts
GET /accounts/{id}
PUT /accounts/{id}
DELETE /accounts/{id}
GET /accounts/{id}/balance?from=YYYY-MM-DD&to=YYYY-MM-DD
```

### Categories

```text
GET /categories
POST /categories
GET /categories/{id}
PUT /categories/{id}
DELETE /categories/{id}
```

### Transactions

```text
GET /transactions?page=&size=&sort=&startDate=YYYY-MM-DD&endDate=YYYY-MM-DD&type=&accountId=&categoryId=&active=
POST /transactions
POST /transactions/installments
GET /transactions/{id}
PUT /transactions/{id}
DELETE /transactions/{id}
```

Transferencia:

```text
POST /transactions/transfers
```

Cartao:

```text
GET /transactions/card-summary?billingMonth=YYYY-MM
```

### Budgets

```text
GET /budgets/{month}
PUT /budgets/{month}
PUT /budgets/{month}/categories/{categoryId}
DELETE /budgets/{month}/categories/{categoryId}
GET /budgets/{month}/summary
```

### Goals

```text
GET /goals
POST /goals
GET /goals/{id}
PUT /goals/{id}
DELETE /goals/{id}
PATCH /goals/{id}/progress
```

### Dashboard

```text
GET /reports/monthly-summary?year=YYYY&month=M
GET /dashboard/monthly?month=YYYY-MM
GET /dashboard/categories?month=YYYY-MM
GET /dashboard/cash-flow?from=YYYY-MM&to=YYYY-MM
```

## PostgreSQL

Recomendacoes:

- Usar UUID como PK.
- Usar `numeric(14,2)` para dinheiro.
- Usar `BigDecimal` no Java para valores monetarios.
- Nunca usar `double` ou `float` para dinheiro.
- Normalizar valores monetarios para 2 casas decimais.
- Usar `date` para data financeira.
- Usar `LocalDate` no Java para datas financeiras.
- Usar `Instant` no Java para auditoria.
- Padronizar auditoria em UTC.
- Criar indices por `user_id`, `transaction_date`, `billing_month`, `account_id`, `category_id`.
- Filtrar `deleted_at is null` nas consultas de transacoes.
- Usar migrations com Flyway ou Liquibase. Recomendacao: Flyway pela simplicidade.

Indices iniciais:

```text
transactions(user_id, transaction_date)
transactions(user_id, billing_month)
transactions(user_id, account_id)
transactions(user_id, category_id)
categories(user_id, name)
accounts(user_id, type)
monthly_budgets(user_id, month)
```

## IA Futura

Principio: IA nunca deve ser fonte de verdade.

Permitido no futuro:

- Sugerir categoria.
- Sugerir merchant normalizado.
- Explicar gastos.
- Responder perguntas com base em dados consultados do PostgreSQL.
- Gerar insights e alertas.
- Sugerir orcamento.

Nao permitido sem confirmacao:

- Criar transacao.
- Editar transacao.
- Excluir transacao.
- Alterar orcamento.
- Alterar meta.

Preparacao ja incluida:

- `merchant_name`
- `raw_description`
- `category_origin`
- `source`
- `external_id`
- `import_batch_id`

Modulo futuro:

```text
ai/
  AiSuggestionService
  AiAnalysisService
  AiChatService
```

Endpoints futuros:

```text
POST /ai/categorize-transaction
POST /ai/analyze-month
POST /ai/chat
```

## Importacao Futura

Ordem recomendada:

1. CSV.
2. XLSX.
3. PDF.
4. Open Finance.

Modelo futuro:

### ImportBatch

```text
id UUID PK
user_id UUID FK -> users.id
account_id UUID FK -> accounts.id
source varchar -- CSV_IMPORT, XLSX_IMPORT, PDF_IMPORT, OPEN_FINANCE
file_name varchar nullable
status varchar -- PENDING, PROCESSED, FAILED
created_at timestamp
processed_at timestamp nullable
```

Fluxo futuro:

1. Upload do arquivo.
2. Parser extrai linhas brutas.
3. Normalizador converte em candidatos de transacao.
4. Deduplicador identifica possiveis duplicatas.
5. Usuario revisa.
6. Sistema grava transacoes confirmadas.

Bibliotecas provaveis:

- CSV: OpenCSV.
- XLSX: Apache POI.
- PDF: PDFBox.

## Frontend PWA

Stack escolhida: Next.js.

Usar Next.js sem depender de SSR para dados privados no MVP. O app pode consumir a API Spring Boot como cliente autenticado, mantendo a experiencia de PWA mobile-first.

Telas iniciais:

- `/login`
- `/dashboard`
- `/transactions`
- `/transactions/new`
- `/accounts`
- `/categories`
- `/budgets`
- `/goals`

PWA:

- Manifest.
- Icones.
- Tema mobile.
- Layout responsivo.
- Service worker depois que o app basico funcionar.

UX prioritaria:

- Botao grande para lancar gasto.
- Cadastro rapido com valor, data, categoria, conta e descricao.
- Dashboard mensal simples.
- Filtros faceis por mes.

## Ordem Pratica De Desenvolvimento

### Fase 1 - Base

1. Criar projeto Spring Boot.
2. Configurar PostgreSQL via Docker Compose.
3. Configurar Flyway.
4. Criar `User`.
5. Criar auth JWT. `[feito]`
6. Criar seed de usuario inicial. `[feito]`
7. Associar dados financeiros ao usuario autenticado. `[proximo]`

### Fase 2 - Cadastros

1. Criar `Account`.
2. Criar `Category`.
3. Criar seed de categorias.
4. Criar endpoints CRUD.

### Fase 3 - Transacoes

1. Criar `Transaction`.
2. Implementar receita/despesa.
3. Implementar transferencia.
4. Implementar parcelamento simples.
5. Implementar filtros.
6. Implementar resumo de cartao por `billingMonth`.

### Fase 4 - Planejamento

1. Criar orcamento mensal.
2. Criar orcamento por categoria.
3. Criar metas.
4. Criar progresso manual das metas.

### Fase 5 - Dashboard

1. Resumo mensal.
2. Gastos por categoria.
3. Comparativo orcado vs realizado.
4. Fluxo mensal.

### Fase 6 - Frontend PWA

1. Criar app frontend.
2. Login.
3. Layout mobile-first.
4. Dashboard.
5. CRUD de transacoes.
6. Orcamentos.
7. Metas.
8. Ajustes PWA.

### Fase 7 - Pos-MVP

1. Importacao CSV.
2. Sugestao de categoria por IA.
3. Chat financeiro com dados do banco.
4. XLSX.
5. PDF.
6. Investimentos detalhados.
7. Open Finance.
8. Exportacao CSV.
9. Historico completo de alteracoes.
10. Anexos e comprovantes.
11. Notificacoes e lembretes.
12. Lancamentos recorrentes.
13. Tags em transacoes.

## Perguntas Ainda Em Aberto

- Usar Flyway ou Liquibase?
