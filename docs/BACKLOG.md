# Eco - Backlog Tecnico Do MVP

## Fase 0 - Preparacao

- [ ] Inicializar repositorio Git.
- [ ] Criar projeto Spring Boot.
- [ ] Definir package base `com.eco`.
- [ ] Adicionar dependencias iniciais:
  - Spring Web
  - Spring Data JPA
  - Spring Security
  - PostgreSQL Driver
  - Flyway
  - Bean Validation
  - Springdoc OpenAPI
  - Lombok, se quiser usar
- [ ] Criar `docker-compose.yml` com PostgreSQL.
- [ ] Criar perfis `local` e `test`.
- [ ] Configurar `application.yml`.
- [ ] Subir banco local.
- [ ] Validar conexao da aplicacao com PostgreSQL.

## Fase 1 - Base De Banco

- [ ] Criar migration `users`.
- [ ] Criar migration `refresh_tokens`.
- [ ] Criar migration `accounts`.
- [ ] Criar migration `categories`.
- [ ] Criar migration `transactions`.
- [ ] Criar migration `monthly_budgets`.
- [ ] Criar migration `category_budgets`.
- [ ] Criar migration `goals`.
- [ ] Criar indices principais.
- [ ] Criar seed local de usuario inicial.
- [ ] Criar seed de categorias padrao.

## Fase 2 - Common E Infra

- [ ] Criar estrutura `common`.
- [ ] Criar resposta padrao de erro.
- [ ] Criar `@RestControllerAdvice`.
- [ ] Criar excecoes:
  - `NotFoundException`
  - `BusinessRuleException`
  - `UnauthorizedException`
- [ ] Criar base de auditoria para `createdAt` e `updatedAt`.
- [ ] Definir estrategia de soft delete para transacoes.
- [ ] Configurar Swagger/OpenAPI local.

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

- [ ] Criar entidade `Account`.
- [ ] Criar enum `AccountType`.
- [ ] Criar DTOs.
- [ ] Criar repository.
- [ ] Criar service.
- [ ] Criar controller.
- [ ] Implementar `GET /accounts`.
- [ ] Implementar `POST /accounts`.
- [ ] Implementar `GET /accounts/{id}`.
- [ ] Implementar `PUT /accounts/{id}`.
- [ ] Implementar arquivamento por `DELETE /accounts/{id}`.
- [ ] Implementar calculo de saldo com `initialBalance`.
- [ ] Implementar `GET /accounts/{id}/balance`.
- [ ] Criar testes de service.

## Fase 5 - Categories

- [ ] Criar entidade `Category`.
- [ ] Criar enum `CategoryKind`.
- [ ] Criar DTOs.
- [ ] Criar repository.
- [ ] Criar service.
- [ ] Criar controller.
- [ ] Implementar `GET /categories`.
- [ ] Implementar `POST /categories`.
- [ ] Implementar `GET /categories/{id}`.
- [ ] Implementar `PUT /categories/{id}`.
- [ ] Implementar arquivamento por `DELETE /categories/{id}`.
- [ ] Impedir nome duplicado por usuario.
- [ ] Criar testes de service.

## Fase 6 - Transactions

- [ ] Criar entidade `Transaction`.
- [ ] Criar enums:
  - `TransactionType`
  - `TransactionSource`
  - `CategoryOrigin`
- [ ] Criar DTOs de criacao, edicao, listagem e filtros.
- [ ] Criar repository com filtros por periodo, tipo, conta, categoria e texto.
- [ ] Criar service.
- [ ] Criar controller.
- [ ] Implementar receita.
- [ ] Implementar despesa.
- [ ] Validar `amount > 0`.
- [ ] Validar `billingMonth` obrigatorio para despesas em cartao.
- [ ] Implementar edicao.
- [ ] Implementar soft delete.
- [ ] Implementar listagem por periodo.
- [ ] Implementar busca simples por `description`, `merchantName`, `rawDescription`.
- [ ] Criar testes de regras principais.

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

- [ ] Criar DTO de resumo mensal.
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

- [ ] Revisar Swagger.
- [ ] Criar exemplos de request/response.
- [ ] Criar arquivo `API_CONTRACT.md`, se Swagger nao for suficiente.
- [ ] Validar fluxo:
  - login
  - listar contas
  - listar categorias
  - criar transacao
  - listar dashboard

## Fase 14 - Frontend Next.js

- [ ] Criar app Next.js.
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

