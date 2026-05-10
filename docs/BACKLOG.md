# Eco - Backlog Tecnico Do MVP

## Fase 0 - Preparacao

- [x] Inicializar repositorio Git.
- [x] Criar projeto Spring Boot.
- [x] Definir package base `com.eco`.
- [x] Adicionar dependencias iniciais:
  - Spring Web
  - Spring Data JPA
  - Spring Security
  - PostgreSQL Driver
  - Flyway
  - Bean Validation
  - Springdoc OpenAPI
  - Lombok, se quiser usar
- [x] Criar `docker-compose.yml` com PostgreSQL.
- [ ] Criar perfis `local` e `test`.
- [x] Configurar `application.yaml`.
- [x] Subir banco local.
- [x] Validar conexao da aplicacao com PostgreSQL.

## Fase 1 - Base De Banco

- [ ] Criar migration `users`.
- [ ] Criar migration `refresh_tokens`.
- [x] Criar migration `accounts`.
- [x] Criar migration `categories`.
- [x] Criar migration `transactions`.
- [ ] Criar migration `monthly_budgets`.
- [ ] Criar migration `category_budgets`.
- [ ] Criar migration `goals`.
- [ ] Criar indices principais.
- [ ] Criar seed local de usuario inicial.
- [ ] Criar seed de categorias padrao.

## Fase 2 - Common E Infra

- [x] Criar estrutura `common`.
- [x] Criar resposta padrao de erro.
- [x] Criar `@RestControllerAdvice`.
- [ ] Criar excecoes:
  - [x] `NotFoundException`
  - [x] `BusinessException`
  - `UnauthorizedException`
- [ ] Criar base de auditoria para `createdAt` e `updatedAt`.
- [ ] Definir estrategia de soft delete para transacoes.
- [x] Configurar Swagger/OpenAPI local.

## Fase 3 - Autenticacao

- [ ] Criar entidade `User`.
- [ ] Criar entidade `RefreshToken`.
- [ ] Criar repositories.
- [ ] Criar DTOs de login, refresh e usuario logado.
- [ ] Implementar password hashing com BCrypt.
- [ ] Implementar geracao de access token.
- [ ] Implementar geracao e armazenamento de refresh token com hash.
- [ ] Implementar `POST /auth/login`.
- [ ] Implementar `POST /auth/refresh`.
- [ ] Implementar `POST /auth/logout`.
- [ ] Implementar `GET /auth/me`.
- [ ] Configurar filtro JWT no Spring Security.
- [ ] Proteger endpoints privados.
- [ ] Testar login e refresh.

## Fase 4 - Accounts

- [x] Criar entidade `Account`.
- [x] Criar enum `AccountType`.
- [x] Criar DTOs.
- [x] Criar repository.
- [x] Criar service.
- [x] Criar controller.
- [x] Implementar `GET /accounts`.
- [x] Implementar `POST /accounts`.
- [x] Implementar `GET /accounts/{id}`.
- [x] Implementar `PUT /accounts/{id}`.
- [x] Implementar arquivamento por `DELETE /accounts/{id}`.
- [ ] Implementar calculo de saldo com `initialBalance`.
- [ ] Implementar `GET /accounts/{id}/balance`.
- [x] Criar testes de service.

## Fase 5 - Categories

- [x] Criar entidade `Category`.
- [x] Criar enum `CategoryKind`.
- [x] Criar DTOs.
- [x] Criar repository.
- [x] Criar service.
- [x] Criar controller.
- [x] Implementar `GET /categories`.
- [x] Implementar `POST /categories`.
- [x] Implementar `GET /categories/{id}`.
- [x] Implementar `PUT /categories/{id}`.
- [x] Implementar arquivamento por `DELETE /categories/{id}`.
- [x] Impedir nome duplicado globalmente no estado atual.
- [x] Criar testes de service.

## Fase 6 - Transactions

- [x] Criar entidade `Transaction`.
- [ ] Criar enums:
- [x] `TransactionType`
  - `TransactionSource`
  - `CategoryOrigin`
- [x] Criar DTOs de criacao, edicao, listagem e pagina.
- [x] Criar repository com filtros por periodo, tipo, conta e categoria.
- [x] Criar service.
- [x] Criar controller.
- [x] Implementar receita.
- [x] Implementar despesa.
- [x] Validar `amount > 0`.
- [ ] Validar `billingMonth` obrigatorio para despesas em cartao.
- [x] Implementar edicao.
- [x] Implementar soft delete com `active=false`.
- [x] Implementar listagem por periodo.
- [x] Implementar paginacao.
- [x] Validar compatibilidade categoria x transacao.
- [ ] Implementar busca simples por `description`, `merchantName`, `rawDescription`.
- [x] Criar testes de regras principais.

## Fase 7 - Transferencias

- [ ] Implementar `POST /transactions/transfers`.
- [ ] Validar conta de origem.
- [ ] Validar conta de destino.
- [ ] Bloquear origem igual ao destino.
- [ ] Salvar transferencia como uma transacao unica.
- [ ] Ajustar calculo de saldo para considerar transferencias.
- [ ] Testar saldo da origem.
- [ ] Testar saldo do destino.

## Fase 8 - Parcelamento

- [ ] Criar DTO `CreateInstallmentTransactionRequest`.
- [ ] Implementar `POST /transactions/installments`.
- [ ] Gerar `installmentGroupId`.
- [ ] Criar N transacoes futuras.
- [ ] Preencher `installmentNumber`.
- [ ] Preencher `installmentTotal`.
- [ ] Calcular valor de cada parcela.
- [ ] Tratar arredondamento da ultima parcela.
- [ ] Calcular `billingMonth` de cada parcela.
- [ ] Testar parcelamento em 2x.
- [ ] Testar parcelamento com centavos quebrados.

## Fase 9 - Cartao

- [ ] Implementar `GET /transactions/card-summary?billingMonth=YYYY-MM`.
- [ ] Somar despesas por `billingMonth`.
- [ ] Ignorar transacoes deletadas.
- [ ] Garantir que dashboard use `billingMonth` para cartao.
- [ ] Testar compra em data diferente do mes de fatura.

## Fase 10 - Budgets

- [ ] Criar entidade `MonthlyBudget`.
- [ ] Criar entidade `CategoryBudget`.
- [ ] Criar DTOs.
- [ ] Criar repositories.
- [ ] Criar service.
- [ ] Criar controller.
- [ ] Implementar `GET /budgets/{month}`.
- [ ] Implementar `PUT /budgets/{month}`.
- [ ] Implementar `PUT /budgets/{month}/categories/{categoryId}`.
- [ ] Implementar `DELETE /budgets/{month}/categories/{categoryId}`.
- [ ] Implementar `GET /budgets/{month}/summary`.
- [ ] Considerar apenas despesas.
- [ ] Considerar cartao pelo `billingMonth`.
- [ ] Testar categoria dentro do limite.
- [ ] Testar categoria acima do limite.

## Fase 11 - Goals

- [ ] Criar entidade `Goal`.
- [ ] Criar enum `GoalStatus`.
- [ ] Criar DTOs.
- [ ] Criar repository.
- [ ] Criar service.
- [ ] Criar controller.
- [ ] Implementar `GET /goals`.
- [ ] Implementar `POST /goals`.
- [ ] Implementar `GET /goals/{id}`.
- [ ] Implementar `PUT /goals/{id}`.
- [ ] Implementar arquivamento por `DELETE /goals/{id}`.
- [ ] Implementar `PATCH /goals/{id}/progress`.
- [ ] Testar progresso manual.
- [ ] Testar conclusao.

## Fase 12 - Dashboard

- [x] Criar DTO de resumo mensal em `report`.
- [x] Implementar `GET /reports/monthly-summary?year=&month=`.
- [x] Testar service de resumo mensal.
- [x] Testar controller de resumo mensal.
- [ ] Implementar `GET /dashboard/monthly?month=YYYY-MM`.
- [ ] Implementar `GET /dashboard/categories?month=YYYY-MM`.
- [ ] Implementar `GET /dashboard/cash-flow?from=YYYY-MM&to=YYYY-MM`.
- [ ] Calcular receitas por `transactionDate`.
- [ ] Calcular despesas comuns por `transactionDate`.
- [ ] Calcular despesas de cartao por `billingMonth`.
- [ ] Calcular resultado mensal.
- [ ] Calcular orcamento consumido.
- [ ] Retornar progresso das metas.
- [ ] Testar resumo mensal.

## Fase 13 - Contrato Frontend

- [x] Revisar Swagger/API local durante implementacao.
- [x] Criar exemplos de request/response.
- [x] Criar arquivo `API_CONTRACT.md`.
- [ ] Validar fluxo:
  - login
  - listar contas
  - listar categorias
  - criar transacao
  - listar dashboard

## Fase 14 - Frontend Next.js

- [x] Criar app Next.js mockado.
- [ ] Configurar PWA base.
- [ ] Criar tela `/login`.
- [ ] Criar client HTTP autenticado.
- [ ] Implementar refresh token.
- [ ] Criar layout mobile-first.
- [ ] Criar `/dashboard`.
- [ ] Criar `/transactions`.
- [ ] Criar `/transactions/new`.
- [ ] Criar `/accounts`.
- [ ] Criar `/categories`.
- [ ] Criar `/budgets`.
- [ ] Criar `/goals`.
- [ ] Testar fluxo diario no celular.

Nota: a proxima integracao frontend sera feita no opencode com Kimi 2.6. Usar `API_CONTRACT.md` e `frontend/README.md` como handoff.

## Fase 15 - Pos-MVP

- [ ] Importacao CSV.
- [ ] Sugestao de categoria por IA.
- [ ] Analise mensal por IA.
- [ ] Chat financeiro com dados do PostgreSQL.
- [ ] Importacao XLSX.
- [ ] Importacao PDF.
- [ ] Investimentos detalhados.
- [ ] Open Finance.
- [ ] Exportacao CSV.
- [ ] Historico completo de alteracoes.
- [ ] Anexos.
- [ ] Notificacoes.
- [ ] Lancamentos recorrentes.
- [ ] Tags.
