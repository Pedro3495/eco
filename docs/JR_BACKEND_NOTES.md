# Eco - Anotacoes Backend Para Dev Jr

Este arquivo resume o que voce precisa saber explicar sobre o backend atual do Eco.

Objetivo: entender o projeto, nao decorar codigo.

## Visao Geral

O Eco e uma API REST de financas pessoais.

Stack atual:

- Java 21
- Spring Boot 3.5.14
- Spring Web
- Spring Data JPA
- Spring Security
- Bean Validation
- PostgreSQL
- Flyway
- Maven
- JUnit e Mockito
- GitHub Actions

Estrutura principal:

```text
backend/
  src/main/java/com/eco/
    account/
    category/
    transaction/
    common/exception/
    config/
  src/main/resources/
    application.yaml
    db/migration/
```

Fluxo mental mais importante:

```text
HTTP request
-> Controller
-> Request DTO
-> Service
-> Repository
-> Entity
-> Banco
-> Entity
-> Response DTO
-> HTTP response
```

## O Que Cada Camada Faz

### Controller

Responsabilidade:

```text
Receber HTTP e devolver HTTP.
```

No projeto:

- `CategoryController`
- `AccountController`
- `TransactionController`
- `ReportController`

O controller deve:

- definir a rota;
- receber path params, query params e body;
- chamar o service;
- retornar DTO de resposta;
- definir status HTTP quando necessario.

Exemplos:

```java
@GetMapping("/{id}")
public TransactionResponse findById(@PathVariable UUID id) {
    return transactionService.findById(id);
}
```

```java
@PostMapping
@ResponseStatus(HttpStatus.CREATED)
public TransactionResponse create(@RequestBody @Valid CreateTransactionRequest request) {
    return transactionService.create(request);
}
```

O controller nao deve concentrar regra de negocio.

Regra simples:

```text
Controller coordena entrada e saida.
Service decide o que pode ou nao pode.
```

## Service

Responsabilidade:

```text
Guardar regra de negocio e coordenar operacoes.
```

No projeto:

- `CategoryService`
- `AccountService`
- `TransactionService`
- `ReportService`

O service faz coisas como:

- buscar entidade por id;
- validar duplicidade de nome;
- criar entidades;
- editar entidades;
- desativar registro;
- buscar relacionamentos obrigatorios;
- montar filtros dinamicos.

Exemplo:

```java
if (accountRepository.existsByNameIgnoreCase(request.getName())) {
    throw new BusinessException("Conta ja existe");
}
```

Isso e regra de negocio. Por isso fica no service.

### `@Transactional`

Usado nos services.

```java
@Transactional(readOnly = true)
```

Para consultas. Diz que nao vai alterar banco.

```java
@Transactional
```

Para criacao, edicao ou desativacao.

Ideia principal:

```text
Uma operacao de negocio deve acontecer dentro de uma transacao.
```

## Repository

Responsabilidade:

```text
Acessar o banco de dados.
```

No projeto:

- `CategoryRepository`
- `AccountRepository`
- `TransactionRepository`

Eles estendem `JpaRepository`.

```java
public interface AccountRepository extends JpaRepository<Account, UUID> {
}
```

O `JpaRepository` ja entrega:

- `save`
- `findById`
- `findAll`
- `delete`
- `existsById`
- `count`

Tambem da para criar metodos por nome:

```java
boolean existsByNameIgnoreCase(String name);
Optional<Account> findByNameIgnoreCase(String name);
```

O Spring Data JPA interpreta o nome do metodo e monta a query.

## Entity

Responsabilidade:

```text
Representar uma tabela do banco no Java.
```

No projeto:

- `Category`
- `Account`
- `Transaction`

Exemplo:

```java
@Entity
@Table(name = "transactions")
public class Transaction {
}
```

Uma entity tem:

- `@Id`;
- campos iguais ou equivalentes as colunas;
- `@Column`;
- relacionamentos JPA;
- construtor vazio protegido para o Hibernate;
- getters e setters.

Importante:

```text
Entity nao e contrato da API.
Entity representa persistencia.
DTO representa entrada/saida HTTP.
```

## DTO

DTO significa:

```text
Data Transfer Object
```

No projeto existem DTOs de entrada e saida:

- `CreateCategoryRequest`
- `UpdateCategoryRequest`
- `CategoryResponse`
- `CreateAccountRequest`
- `UpdateAccountRequest`
- `AccountResponse`
- `CreateTransactionRequest`
- `UpdateTransactionRequest`
- `TransactionResponse`

### Request DTO

Recebe dados do usuario.

Exemplo:

```text
CreateTransactionRequest
```

Usado em:

```text
POST /api/transactions
```

Tem validacoes como:

- `@NotBlank`
- `@NotNull`
- `@Positive`
- `@Size`

### Response DTO

Controla o que a API devolve.

Exemplo:

```java
public static TransactionResponse fromEntity(Transaction transaction)
```

Esse metodo converte:

```text
Entity -> DTO de resposta
```

Motivos para usar DTO:

- nao expor entity diretamente;
- controlar o JSON da API;
- evitar vazar campos internos;
- facilitar validacao;
- evitar problemas com relacionamentos JPA no JSON.

## Migrations E Flyway

Responsabilidade:

```text
Versionar o banco de dados.
```

Arquivos atuais:

- `V1__create_categories_table.sql`
- `V2__create_accounts_table.sql`
- `V3__create_transactions_table.sql`

Regra:

```text
Entity Java precisa bater com migration SQL.
```

No `application.yaml`:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate
```

Isso significa:

```text
Hibernate valida o banco, mas nao cria tabela automaticamente.
Flyway cria e altera as tabelas.
```

Para vaga Jr, saiba explicar:

> Eu uso Flyway para controlar schema do banco por migrations e deixo o Hibernate em validate para detectar divergencias entre entity e banco.

## Relacionamentos JPA

`Transaction` se relaciona com `Account` e `Category`.

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "account_id", nullable = false)
private Account account;
```

Significa:

```text
Muitas transacoes pertencem a uma conta.
```

E:

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "category_id", nullable = false)
private Category category;
```

Significa:

```text
Muitas transacoes pertencem a uma categoria.
```

No banco, isso vira foreign key:

```text
transactions.account_id -> accounts.id
transactions.category_id -> categories.id
```

## Soft Delete

O projeto nao apaga fisicamente categorias, contas e transacoes.

Ele marca:

```text
active = false
```

Exemplo:

```java
transaction.setActive(false);
transactionRepository.save(transaction);
```

Ideia:

```text
DELETE na API arquiva/desativa o registro.
```

Isso e comum em sistemas financeiros porque historico importa.

## Validacao

Validacao acontece nos DTOs com Bean Validation.

No controller:

```java
@RequestBody @Valid CreateTransactionRequest request
```

`@Valid` manda o Spring validar o DTO antes de chamar o service.

Se falhar, cai no:

```text
GlobalExceptionHandler.handleValidation
```

E retorna `400 Bad Request` com campos invalidos.

## Tratamento De Erros

Pacote:

```text
com.eco.common.exception
```

Classes:

- `NotFoundException`
- `BusinessException`
- `ErrorResponse`
- `GlobalExceptionHandler`

Ideia:

```text
Service joga exception.
GlobalExceptionHandler transforma exception em resposta HTTP.
```

Exemplo:

```java
throw new NotFoundException("Transacao nao encontrada");
```

Resposta esperada:

```text
404 Not Found
```

Ponto de atencao atual:

```java
@ResponseStatus(HttpStatus.BAD_REQUEST)
```

`BusinessException` deve virar `400 Bad Request`, porque representa erro de regra de negocio ou entrada invalida.

## Security

Spring Security ja esta instalado.

Arquivo:

```text
SecurityConfig.java
```

Configuracao atual:

```java
.authorizeHttpRequests(auth -> auth
    .anyRequest().permitAll()
)
```

Significa:

```text
Por enquanto todos os endpoints estao liberados.
```

Isso e temporario.

Futuro:

- login;
- JWT;
- endpoints protegidos;
- filtrar dados por usuario autenticado.

Para entrevista:

> O projeto ja tem Spring Security configurado, mas nesta fase esta liberado para facilitar desenvolvimento. Autenticacao JWT fica para a proxima etapa.

## Filtros De Transactions

Endpoint:

```text
GET /api/transactions
```

Filtros atuais:

- `accountId`
- `categoryId`
- `type`
- `startDate`
- `endDate`
- `active`
- `page`
- `size`
- `sort`

Exemplos:

```text
GET /api/transactions?type=EXPENSE
GET /api/transactions?active=false
GET /api/transactions?startDate=2026-05-01&endDate=2026-05-31
GET /api/transactions?accountId=<uuid>&type=EXPENSE&active=true
GET /api/transactions?page=0&size=10
```

### Como Funciona

Controller recebe query params:

```java
@RequestParam(required = false) TransactionType type
```

Service monta uma `Specification`.

Repository executa com filtro e paginacao:

```java
transactionRepository.findAll(specification, pageable)
```

Para isso, `TransactionRepository` estende:

```java
JpaSpecificationExecutor<Transaction>
```

Frase para guardar:

```text
Query param -> variavel Java -> Specification -> WHERE no SQL.
```

Exemplo:

```text
/api/transactions?type=EXPENSE&active=false
```

Vira algo parecido com:

```sql
WHERE type = 'EXPENSE'
AND active = false
```

Datas:

```text
startDate -> occurred_at >= startDate
endDate   -> occurred_at <= endDate
```

Se um parametro nao e enviado, ele vem `null` e o filtro e ignorado.

### Paginacao

`GET /api/transactions` retorna uma pagina, nao uma lista direta.

Formato:

```json
{
  "items": [],
  "page": 0,
  "size": 10,
  "totalItems": 0,
  "totalPages": 0
}
```

Fluxo:

```text
Controller recebe Pageable.
Service passa Pageable para o repository.
Repository retorna Page<Transaction>.
Service converte Page<Transaction> em TransactionPageResponse.
```

Padrao atual:

```text
size=10
sort=occurredAt DESC
page=0 para primeira pagina
```

## Reports

Endpoint atual:

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

No teste HTTP real, esse resultado veio zerado porque a transacao existente estava `active=false`. A query do relatorio soma apenas transacoes ativas.

### Como Funciona

Controller:

```text
ReportController recebe year e month por query param.
```

Service:

```text
ReportService calcula startDate e endDate do mes.
```

Repository:

```text
TransactionRepository soma amount por tipo e periodo.
```

Query mental:

```sql
select sum(amount)
from transactions
where type = 'EXPENSE'
  and active = true
  and occurred_at between startDate and endDate
```

Calculo:

```text
balance = income - expense
```

Ponto importante:

```text
Dinheiro usa BigDecimal, nao double.
```

## Testes

Testes atuais:

- `CategoryServiceTest`
- `AccountServiceTest`
- `TransactionServiceTest`
- `ReportServiceTest`
- `ReportControllerTest`
- `EcoBackendApplicationTests`

### Teste Unitario De Service

Usa:

- JUnit
- Mockito
- AssertJ

Objetivo:

```text
Testar regra de negocio sem depender do banco real.
```

Exemplo mental:

```text
Dado que o repository retorna uma conta,
quando criar conta,
entao o service salva e devolve response.
```

Mockito simula dependencies:

```java
@Mock
private AccountRepository accountRepository;

@InjectMocks
private AccountService accountService;
```

### Teste De Contexto

`EcoBackendApplicationTests` sobe o Spring.

Ele ajuda a detectar:

- erro de configuracao;
- migration quebrada;
- entity divergente do banco;
- bean faltando.

### Resultado Atual

Ultimo teste local:

```text
Tests run: 23
Failures: 0
Errors: 0
BUILD SUCCESS
```

## HTTP Status Que Voce Deve Saber

Principais no projeto:

```text
200 OK       -> busca ou atualizacao com resposta
201 Created  -> criacao
204 No Content -> delete/desativacao sem corpo
400 Bad Request -> validacao ou regra de negocio invalida
404 Not Found -> recurso nao encontrado
```

## O Que Voce Deve Saber Explicar Como Jr

Voce deve conseguir responder:

1. O que um controller faz?
2. O que um service faz?
3. O que um repository faz?
4. Por que usamos DTO em vez de devolver entity?
5. O que e uma entity?
6. O que e uma migration?
7. Por que `ddl-auto=validate` e mais seguro que deixar o Hibernate criar tudo?
8. O que `@Transactional` faz?
9. O que `@Valid` faz?
10. O que acontece quando uma validacao falha?
11. O que e soft delete?
12. Como `Transaction` se relaciona com `Account` e `Category`?
13. Como funcionam os filtros opcionais de transactions?
14. Por que usamos `BigDecimal` para dinheiro?
15. Por que teste unitario de service usa mock?
16. Qual a diferenca entre teste de service e teste de controller?
17. O que e `Pageable`?
18. Por que uma listagem real deve ser paginada?

## Respostas Curtas Para Entrevista

### Controller

```text
Controller recebe requisicoes HTTP, extrai parametros/body, chama o service e devolve uma resposta HTTP.
```

### Service

```text
Service concentra regra de negocio e orquestra repositories.
```

### Repository

```text
Repository e a camada de acesso ao banco. No projeto uso Spring Data JPA com JpaRepository.
```

### Entity

```text
Entity representa uma tabela do banco no Java e e gerenciada pelo JPA/Hibernate.
```

### DTO

```text
DTO define o contrato de entrada e saida da API, evitando expor entity diretamente.
```

### Flyway

```text
Flyway versiona alteracoes no banco usando migrations SQL.
```

### `ddl-auto=validate`

```text
Faz o Hibernate validar se as entidades batem com o banco, sem criar ou alterar tabelas automaticamente.
```

### `@Transactional`

```text
Define que uma operacao deve executar dentro de uma transacao no banco.
```

### `@Valid`

```text
Ativa validacao dos DTOs antes de entrar no service.
```

### Specification

```text
Specification permite montar consultas dinamicas. Cada filtro opcional vira uma condicao no WHERE.
```

### MockMvc

```text
MockMvc permite testar um endpoint HTTP sem subir servidor real.
```

### Pageable

```text
Pageable representa page, size e sort recebidos na API e usados pelo repository para limitar a consulta.
```

## Pontos De Atencao Para Melhorar Depois

1. Conectar frontend ao backend real usando opencode/Kimi.
2. Criar telas/formularios reais para categorias, contas e transacoes.
3. Implementar autenticacao JWT.
4. Garantir que, no futuro, todas as consultas filtrem pelo usuario autenticado.

## Checklist Pessoal

Antes de dizer que entende uma feature, tente explicar:

```text
1. Qual endpoint chama?
2. Qual controller recebe?
3. Qual DTO entra?
4. Qual service processa?
5. Qual regra de negocio existe?
6. Qual repository consulta/salva?
7. Qual entity representa o banco?
8. Qual DTO sai?
9. Qual status HTTP retorna?
10. Qual teste cobre isso?
```

Se voce consegue responder isso, voce esta acompanhando bem.
