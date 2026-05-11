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

- [x] Criar migration `users`.
- [x] Criar migration `refresh_tokens`.
- [x] Criar migration `accounts`.
- [x] Criar migration `categories`.
- [x] Criar migration `transactions`.
- [x] Criar migration `monthly_budgets`.
- [x] Criar migration `category_budgets`.
- [x] Criar migration `goals`.
- [ ] Criar indices principais.
- [x] Criar seed local de usuario inicial.
- [ ] Criar seed de categorias padrao.

## Fase 2 - Common E Infra

- [x] Criar estrutura `common`.
- [x] Criar resposta padrao de erro.
- [x] Criar `@RestControllerAdvice`.
- [x] Criar excecoes:
  - [x] `NotFoundException`
  - [x] `BusinessException`
  - [x] `UnauthorizedException`
- [ ] Criar base de auditoria para `createdAt` e `updatedAt`.
- [ ] Definir estrategia de soft delete para transacoes.
- [x] Configurar Swagger/OpenAPI local.

## Fase 3 - Autenticacao

- [x] Criar entidade `User`.
- [x] Criar entidade `RefreshToken`.
- [x] Criar repositories.
- [x] Criar DTOs de login, refresh e usuario logado.
- [x] Implementar password hashing com BCrypt.
- [x] Implementar geracao de access token.
- [x] Implementar geracao e armazenamento de refresh token com hash.
- [x] Implementar `POST /auth/login`.
- [x] Implementar `POST /auth/refresh`.
- [x] Implementar `POST /auth/logout`.
- [x] Implementar `GET /auth/me`.
- [x] Configurar filtro JWT no Spring Security.
- [x] Proteger endpoints privados.
- [x] Testar login e refresh.
- [x] Testar filtro JWT.
- [x] Testar endpoint protegido sem token retornando `401`.

## Fase 3.1 - Escopo Por Usuario

- [x] Adicionar `user_id` em `accounts`.
- [x] Adicionar `user_id` em `categories`.
- [x] Adicionar `user_id` em `transactions`.
- [x] Criar migration para vincular dados existentes ao usuario dev.
- [x] Ajustar repositories para consultar por usuario autenticado.
- [x] Ajustar services para criar dados vinculados ao usuario autenticado.
- [x] Impedir acesso a conta/categoria/transacao de outro usuario.
- [x] Ajustar relatorio mensal para filtrar por usuario.
- [x] Ajustar testes de services para usuario.
- [x] Criar teste de isolamento: usuario A nao ve dados do usuario B.
- [x] Atualizar contrato da API com regra de escopo por usuario.

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
- [x] Implementar calculo de saldo com `initialBalance`.
- [x] Implementar `GET /accounts/{id}/balance`.
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
- [x] Impedir nome duplicado por usuario.
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
- [x] Validar `billingMonth` obrigatorio para despesas em cartao.
- [x] Implementar edicao.
- [x] Implementar soft delete com `active=false`.
- [x] Implementar listagem por periodo.
- [x] Implementar paginacao.
- [x] Validar compatibilidade categoria x transacao.
- [ ] Implementar busca simples por `description`, `merchantName`, `rawDescription`.
- [x] Criar testes de regras principais.

## Fase 7 - Transferencias

- [x] Implementar `POST /transactions/transfers`.
- [x] Validar conta de origem.
- [x] Validar conta de destino.
- [x] Bloquear origem igual ao destino.
- [x] Salvar transferencia como uma transacao unica.
- [x] Ajustar calculo de saldo para considerar transferencias.
- [x] Testar saldo da origem.
- [x] Testar saldo do destino.

## Fase 8 - Parcelamento

- [x] Criar DTO `CreateInstallmentTransactionRequest`.
- [x] Implementar `POST /transactions/installments`.
- [x] Gerar `installmentGroupId`.
- [x] Criar N transacoes futuras.
- [x] Preencher `installmentNumber`.
- [x] Preencher `installmentTotal`.
- [x] Calcular valor de cada parcela.
- [x] Tratar arredondamento da ultima parcela.
- [x] Calcular `billingMonth` de cada parcela.
- [x] Testar parcelamento em 2x.
- [x] Testar parcelamento com centavos quebrados.

## Fase 9 - Cartao

- [x] Implementar `GET /transactions/card-summary?billingMonth=YYYY-MM`.
- [x] Somar despesas por `billingMonth`.
- [x] Ignorar transacoes deletadas.
- [x] Garantir que dashboard use `billingMonth` para cartao.
- [x] Testar compra em data diferente do mes de fatura.

## Fase 10 - Budgets

- [x] Criar entidade `MonthlyBudget`.
- [x] Criar entidade `CategoryBudget`.
- [x] Criar DTOs.
- [x] Criar repositories.
- [x] Criar service.
- [x] Criar controller.
- [x] Implementar `GET /budgets/{month}`.
- [x] Implementar `PUT /budgets/{month}`.
- [x] Implementar `PUT /budgets/{month}/categories/{categoryId}`.
- [x] Implementar `DELETE /budgets/{month}/categories/{categoryId}`.
- [x] Implementar `GET /budgets/{month}/summary`.
- [x] Considerar apenas despesas.
- [x] Considerar cartao pelo `billingMonth`.
- [x] Testar categoria dentro do limite.
- [x] Testar categoria acima do limite.

## Fase 11 - Goals

- [x] Criar entidade `Goal`.
- [x] Criar enum `GoalStatus`.
- [x] Criar DTOs.
- [x] Criar repository.
- [x] Criar service.
- [x] Criar controller.
- [x] Implementar `GET /goals`.
- [x] Implementar `POST /goals`.
- [x] Implementar `GET /goals/{id}`.
- [x] Implementar `PUT /goals/{id}`.
- [x] Implementar arquivamento por `DELETE /goals/{id}`.
- [x] Implementar `PATCH /goals/{id}/progress`.
- [x] Testar progresso manual.
- [x] Testar conclusao.

## Fase 12 - Dashboard

- [x] Criar DTO de resumo mensal em `report`.
- [x] Implementar `GET /reports/monthly-summary?year=&month=`.
- [x] Testar service de resumo mensal.
- [x] Testar controller de resumo mensal.
- [x] Implementar `GET /dashboard/monthly?month=YYYY-MM`.
- [x] Implementar `GET /dashboard/categories?month=YYYY-MM`.
- [x] Implementar `GET /dashboard/cash-flow?from=YYYY-MM&to=YYYY-MM`.
- [x] Calcular receitas por `transactionDate`.
- [x] Calcular despesas comuns por `transactionDate`.
- [x] Calcular despesas de cartao por `billingMonth`.
- [x] Calcular resultado mensal.
- [x] Calcular orcamento consumido.
- [x] Retornar progresso das metas.
- [x] Testar resumo mensal.

## Fase 13 - Contrato Frontend

- [x] Revisar Swagger/API local durante implementacao.
- [x] Criar exemplos de request/response.
- [x] Criar arquivo `API_CONTRACT.md`.
- [x] Validar fluxo:
  - login
  - listar contas
  - listar categorias
  - criar transacao
  - listar dashboard

## Fase 14 - Frontend Next.js

- [x] Criar app Next.js mockado.
- [x] Configurar PWA base.
- [x] Criar tela `/login`.
- [x] Criar client HTTP autenticado.
- [x] Implementar refresh token.
- [x] Criar layout mobile-first.
- [x] Criar `/dashboard`.
- [x] Criar `/transactions`.
- [x] Criar `/transactions/new`.
- [x] Criar `/accounts`.
- [x] Criar `/categories`.
- [x] Criar `/budgets`.
- [x] Criar `/goals`.
- [ ] Testar fluxo diario no celular.

Nota: backend MVP funcional e frontend integrado aos endpoints principais. Ainda falta teste manual em celular real e deploy publico.

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
