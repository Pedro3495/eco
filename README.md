# Eco

Eco e um aplicativo pessoal de controle financeiro com foco em uso diario, organizacao clara dos dados e evolucao futura com inteligencia artificial.

O projeto nasceu para resolver uma necessidade real: acompanhar gastos, receitas, cartao, orcamentos e metas em um PWA acessivel pelo celular e pelo computador. Ao mesmo tempo, funciona como projeto de portfolio e trilha pratica de estudo em Java, Spring Boot, PostgreSQL e desenvolvimento full stack.

## Objetivo

Construir uma plataforma simples, confiavel e evolutiva para financas pessoais.

No MVP, o foco e registrar e visualizar dados manualmente:

- receitas;
- despesas;
- transferencias internas;
- conta bancaria;
- cartao de credito;
- categorias;
- orcamentos mensais;
- metas financeiras;
- dashboard mensal.

Funcionalidades mais complexas, como IA ativa, importacao de arquivos, investimentos detalhados e Open Finance, ficam planejadas para fases futuras.

## Estrutura Do Repositorio

```text
Eco/
  backend/
    docker-compose.yml
    README.md
    TODO_BACKEND.md
  docs/
    MVP_SCOPE.md
    BACKLOG.md
    API_CONTRACT.md
  frontend/
    src/
    package.json
    README.md
```

O backend esta sendo implementado manualmente em Java/Spring Boot. O frontend ja possui uma versao mockada em Next.js e sera integrado ao backend real em uma proxima etapa.

## Stack

### Backend

- Java 21
- Spring Boot 3.5.14
- Spring Web
- Spring Security instalado, temporariamente liberado com `permitAll`
- Spring Data JPA / Hibernate
- Bean Validation
- Springdoc OpenAPI
- Flyway
- JUnit, Mockito e AssertJ
- GitHub Actions CI

### Banco De Dados

- PostgreSQL
- UUID como chave primaria
- `numeric(14,2)` para valores monetarios
- migrations versionadas com Flyway

### Frontend

- Next.js
- PWA mobile-first
- Interface responsiva para desktop

### Infraestrutura

- Docker Compose para ambiente local
- Deploy futuro em plataforma como Render, Railway, Fly.io ou Vercel

### IA Futura

- Integracao com API de LLM
- Sugestao de categorias
- Analise de gastos
- Chat financeiro
- Insights sobre planejamento

## Principios Do Projeto

- Comecar simples e funcional.
- Evitar complexidade prematura.
- Manter dados financeiros confiaveis.
- IA sugere, usuario confirma.
- O banco de dados e a fonte da verdade.
- O backend deve ser claro o suficiente para estudo e forte o suficiente para portfolio.
- Evoluir por fases pequenas e bem testadas.

## Escopo Do MVP

O MVP sera um monolito modular em Spring Boot, com API REST e PostgreSQL.

Funcionalidades previstas:

- autenticacao com JWT;
- cadastro de contas;
- cadastro de categorias;
- lancamento de receitas e despesas;
- transferencia entre contas;
- despesas de cartao com mes de fatura;
- parcelamento simples;
- orcamento mensal por categoria;
- limite geral mensal opcional;
- metas financeiras simples;
- dashboard mensal;
- busca simples em transacoes;
- soft delete para dados financeiros;
- documentacao da API com OpenAPI/Swagger.

Fora do MVP:

- IA ativa;
- importacao CSV/XLSX/PDF;
- Open Finance;
- investimentos detalhados;
- fatura completa de cartao;
- anexos;
- notificacoes;
- recorrencias;
- tags;
- exportacao de dados.

## Arquitetura Backend

Estrutura planejada:

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

Cada modulo deve seguir a estrutura tradicional do Spring:

```text
controller/
service/
repository/
dto/
model/
```

Regras gerais:

- controllers finos;
- services com regras de negocio;
- repositories para acesso ao banco;
- DTOs como contrato externo da API;
- entidades JPA sem regra excessiva;
- erros padronizados com `@RestControllerAdvice`;
- validacao de entrada com Bean Validation.

## Entidades Principais

- `User`
- `RefreshToken`
- `Account`
- `Category`
- `Transaction`
- `MonthlyBudget`
- `CategoryBudget`
- `Goal`

Campos e relacionamentos detalhados estao documentados em [MVP_SCOPE.md](./docs/MVP_SCOPE.md).

## API

Grupos de endpoints atuais/planejados:

- `/api/accounts`
- `/api/categories`
- `/api/transactions`
- `/api/reports`
- `/api/auth` futuro
- `/api/budgets`
- `/api/goals`
- `/api/dashboard`

O contrato inicial da API esta em [API_CONTRACT.md](./docs/API_CONTRACT.md).

## Status Atual

Backend implementado ate agora:

- migrations Flyway para `categories`, `accounts` e `transactions`;
- CRUD de categorias;
- CRUD de contas;
- CRUD de transacoes;
- filtros em `GET /api/transactions`;
- paginacao em `GET /api/transactions`;
- regra de compatibilidade entre tipo da categoria e tipo da transacao;
- resumo mensal em `GET /api/reports/monthly-summary`;
- tratamento global de erros;
- testes unitarios de services;
- primeiro teste de controller com `@WebMvcTest`;
- CI configurado para rodar testes.

Frontend:

- Next.js com tela mockada;
- dados ainda vêm de `frontend/src/mocks/finance-data.ts`;
- a integracao com a API sera feita em outra sessao/modelo, usando este contrato como base.

A ordem sugerida de implementacao esta em [BACKLOG.md](./docs/BACKLOG.md).

## Regras Financeiras Importantes

- Valores monetarios devem usar `BigDecimal` no Java.
- Nunca usar `double` ou `float` para dinheiro.
- `amount` deve ser sempre positivo.
- O tipo da transacao define o sentido financeiro.
- Transacoes usam `LocalDate`.
- Campos de auditoria usam `Instant` em UTC.
- Despesas de cartao usam `billingMonth`.
- Dashboard e orcamento consideram cartao pelo mes da fatura.
- Orcamento considera apenas despesas.
- Transacoes deletadas usam soft delete.

## IA No Projeto

A IA nao entra como fonte de verdade.

No futuro, ela podera:

- sugerir categoria de transacao;
- explicar gastos;
- responder perguntas sobre os dados;
- apontar anomalias;
- sugerir melhorias de planejamento;
- apoiar analise de investimentos.

Ela nao deve:

- criar transacoes sem confirmacao;
- editar dados automaticamente;
- apagar informacoes;
- alterar orcamentos ou metas sem aprovacao.

## Roadmap Futuro

- Importacao CSV.
- Sugestao de categorias com IA.
- Analise mensal com IA.
- Chat financeiro com dados do PostgreSQL.
- Importacao XLSX.
- Importacao PDF.
- Investimentos detalhados.
- Open Finance.
- Exportacao CSV.
- Historico completo de alteracoes.
- Anexos e comprovantes.
- Notificacoes.
- Lancamentos recorrentes.
- Tags.

## Documentos

Documentos atuais:

- [MVP_SCOPE.md](./docs/MVP_SCOPE.md)
- [BACKLOG.md](./docs/BACKLOG.md)
- [API_CONTRACT.md](./docs/API_CONTRACT.md)
- [JR_BACKEND_NOTES.md](./docs/JR_BACKEND_NOTES.md)
