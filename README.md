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

## Stack Planejada

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

O backend fica reservado para implementacao manual em Java/Spring Boot. O frontend ja possui uma versao mockada em Next.js para validar a experiencia visual antes da API estar pronta.

## Stack Planejada

### Backend

- Java
- Spring Boot
- Spring Web
- Spring Security
- JWT com access token e refresh token
- Spring Data JPA / Hibernate
- Bean Validation
- Springdoc OpenAPI
- Flyway

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

Principais grupos de endpoints:

- `/api/auth`
- `/api/accounts`
- `/api/categories`
- `/api/transactions`
- `/api/budgets`
- `/api/goals`
- `/api/dashboard`

O contrato inicial da API esta em [API_CONTRACT.md](./docs/API_CONTRACT.md).

## Backlog

A ordem sugerida de implementacao esta em [BACKLOG.md](./docs/BACKLOG.md).

Resumo das fases:

1. Preparacao do projeto.
2. Banco de dados e migrations.
3. Infra comum e tratamento de erros.
4. Autenticacao.
5. Contas.
6. Categorias.
7. Transacoes.
8. Transferencias.
9. Parcelamento.
10. Cartao.
11. Orcamentos.
12. Metas.
13. Dashboard.
14. Contrato frontend.
15. Frontend PWA.
16. Pos-MVP.

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

## Status

Projeto em fase de planejamento tecnico e preparacao do MVP.

Documentos atuais:

- [MVP_SCOPE.md](./docs/MVP_SCOPE.md)
- [BACKLOG.md](./docs/BACKLOG.md)
- [API_CONTRACT.md](./docs/API_CONTRACT.md)
