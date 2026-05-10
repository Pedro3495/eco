# Eco Backend - Registro De Aprendizado

Resumo atualizado do backend ate o estado atual.

## Estado Atual

Projeto Spring Boot em:

```text
D:\projetos\Eco\backend
```

Stack:

- Java 21
- Maven
- Spring Boot 3.5.14
- Spring Web
- Spring Data JPA
- Spring Security
- Bean Validation
- PostgreSQL
- Flyway
- Springdoc OpenAPI
- JUnit, Mockito e AssertJ

## Configuracao

Arquivo principal:

```text
src/main/resources/application.yaml
```

Pontos importantes:

- `server.servlet.context-path=/api`
- PostgreSQL em `jdbc:postgresql://localhost:5432/eco`
- usuario/senha local: `postgres/postgres`
- `ddl-auto=validate`
- Flyway habilitado

## Migrations

Implementadas:

- `V1__create_categories_table.sql`
- `V2__create_accounts_table.sql`
- `V3__create_transactions_table.sql`

Regra:

```text
Depois que migration roda, nao editar como rascunho. Criar nova V4 para mudancas futuras.
```

## Modulos Implementados

### Category

- CRUD
- validacao com DTO
- nome duplicado
- soft delete com `active=false`
- testes de service

### Account

- CRUD
- nome duplicado
- soft delete com `active=false`
- testes de service

### Transaction

- CRUD de receitas e despesas
- relacionamento com conta
- relacionamento com categoria
- soft delete com `active=false`
- filtros opcionais
- paginacao
- regra categoria x tipo de transacao
- testes de service

Filtros atuais:

```text
accountId
categoryId
type
startDate
endDate
active
page
size
sort
```

Resposta paginada:

```json
{
  "items": [],
  "page": 0,
  "size": 10,
  "totalItems": 0,
  "totalPages": 0
}
```

### Report

Endpoint:

```text
GET /api/reports/monthly-summary?year=2026&month=5
```

Resposta:

```json
{
  "income": 0,
  "expense": 0,
  "balance": 0
}
```

## Camadas

```text
Controller = HTTP
Service = regra de negocio
Repository = banco
Entity = tabela/registro
DTO = contrato da API
Migration = versao do banco
```

## Testes

Testes atuais:

- `CategoryServiceTest`
- `AccountServiceTest`
- `TransactionServiceTest`
- `ReportServiceTest`
- `ReportControllerTest`
- `EcoBackendApplicationTests`

Ultimo estado conhecido:

```text
Tests run: 23
Failures: 0
Errors: 0
BUILD SUCCESS
```

## Conceitos Praticados

- Entity
- DTO
- Repository
- Service
- Controller
- Flyway
- PostgreSQL
- `ddl-auto=validate`
- `@Transactional`
- `@Valid`
- tratamento global de excecoes
- testes unitarios com Mockito
- `@WebMvcTest`
- `MockMvc`
- filtros com `Specification`
- paginacao com `Pageable`
- queries customizadas com `@Query`
- `BigDecimal` para dinheiro

## Proxima Etapa

Frontend sera integrado em outra sessao/modelo usando opencode/Kimi.

Handoff recomendado:

1. Criar client HTTP no frontend.
2. Consumir `/api/reports/monthly-summary`.
3. Consumir `/api/transactions?page=0&size=10`.
4. Substituir mocks progressivamente.

CORS ja foi configurado para `http://localhost:3000`.
