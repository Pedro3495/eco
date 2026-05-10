# Eco - Contrato Inicial Da API

Base URL local:

```text
http://localhost:8080/api
```

Formato:

- JSON.
- Datas financeiras em `YYYY-MM-DD`.
- Meses em `YYYY-MM`.
- Valores monetarios como numero decimal.
- Autenticacao por Bearer Token nos endpoints privados.

Header:

```text
Authorization: Bearer <accessToken>
```

## Erro Padrao

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

## Auth

### POST `/auth/login`

Request:

```json
{
  "email": "usuario@email.com",
  "password": "senha"
}
```

Response `200`:

```json
{
  "accessToken": "jwt-access-token",
  "refreshToken": "refresh-token",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "user": {
    "id": "b8a7f4e1-8a23-4c2a-8c27-442d0aab0001",
    "name": "Usuario",
    "email": "usuario@email.com"
  }
}
```

### POST `/auth/refresh`

Request:

```json
{
  "refreshToken": "refresh-token"
}
```

Response `200`:

```json
{
  "accessToken": "new-jwt-access-token",
  "refreshToken": "new-refresh-token",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

### POST `/auth/logout`

Request:

```json
{
  "refreshToken": "refresh-token"
}
```

Response `204`.

### GET `/auth/me`

Response `200`:

```json
{
  "id": "b8a7f4e1-8a23-4c2a-8c27-442d0aab0001",
  "name": "Usuario",
  "email": "usuario@email.com"
}
```

## Accounts

Enums:

```text
AccountType: CHECKING, CASH, CREDIT_CARD, INVESTMENT
```

### GET `/accounts`

Response `200`:

```json
[
  {
    "id": "4c657ef7-b84d-452e-9e5e-75d5de410001",
    "name": "Conta Principal",
    "type": "CHECKING",
    "currency": "BRL",
    "initialBalance": 1000.00,
    "currentBalance": 1850.25,
    "active": true
  }
]
```

### POST `/accounts`

Request:

```json
{
  "name": "Conta Principal",
  "type": "CHECKING",
  "currency": "BRL",
  "initialBalance": 1000.00
}
```

Response `201`:

```json
{
  "id": "4c657ef7-b84d-452e-9e5e-75d5de410001",
  "name": "Conta Principal",
  "type": "CHECKING",
  "currency": "BRL",
  "initialBalance": 1000.00,
  "currentBalance": 1000.00,
  "active": true
}
```

### GET `/accounts/{id}`

Response `200`: igual ao objeto de conta.

### PUT `/accounts/{id}`

Request:

```json
{
  "name": "Conta Principal",
  "type": "CHECKING",
  "currency": "BRL",
  "initialBalance": 1200.00,
  "active": true
}
```

Response `200`: objeto atualizado.

### DELETE `/accounts/{id}`

Response `204`.

Regra: arquiva a conta, nao apaga fisicamente.

### GET `/accounts/{id}/balance?from=2026-05-01&to=2026-05-31`

Response `200`:

```json
{
  "accountId": "4c657ef7-b84d-452e-9e5e-75d5de410001",
  "initialBalance": 1000.00,
  "income": 3000.00,
  "expense": 1200.50,
  "transferIn": 0.00,
  "transferOut": 300.00,
  "balance": 2499.50
}
```

## Categories

Enums:

```text
CategoryKind: INCOME, EXPENSE, BOTH
```

### GET `/categories`

Response `200`:

```json
[
  {
    "id": "2ef8b7cc-9e57-4a7a-9582-ff2609170001",
    "name": "Alimentacao",
    "kind": "EXPENSE",
    "color": "#E86F51",
    "icon": "utensils",
    "active": true
  }
]
```

### POST `/categories`

Request:

```json
{
  "name": "Alimentacao",
  "kind": "EXPENSE",
  "color": "#E86F51",
  "icon": "utensils"
}
```

Response `201`: objeto de categoria.

### GET `/categories/{id}`

Response `200`: objeto de categoria.

### PUT `/categories/{id}`

Request:

```json
{
  "name": "Mercado",
  "kind": "EXPENSE",
  "color": "#E86F51",
  "icon": "shopping-cart",
  "active": true
}
```

Response `200`: objeto atualizado.

### DELETE `/categories/{id}`

Response `204`.

Regra: arquiva a categoria.

## Transactions

Enums:

```text
TransactionType atual: INCOME, EXPENSE
Planejado futuro: TRANSFER, TransactionSource, CategoryOrigin
```

### GET `/transactions`

Query params:

```text
page=0
size=10
sort=occurredAt,desc
startDate=2026-05-01
endDate=2026-05-31
type=EXPENSE
accountId=uuid
categoryId=uuid
active=true
```

Response `200`:

```json
{
  "items": [
    {
      "id": "2bcb6ec7-16e4-4f1a-b387-350cf8690001",
      "description": "Mercado",
      "amount": 85.90,
      "type": "EXPENSE",
      "occurredAt": "2026-05-05",
      "accountId": "4c657ef7-b84d-452e-9e5e-75d5de410001",
      "accountName": "Conta Principal",
      "categoryId": "2ef8b7cc-9e57-4a7a-9582-ff2609170001",
      "categoryName": "Alimentacao",
      "note": "Compra semanal",
      "active": true
    }
  ],
  "page": 0,
  "size": 10,
  "totalItems": 1,
  "totalPages": 1
}
```

Paginacao atual:

- Padrao: `size=10`.
- Ordenacao padrao: `occurredAt DESC`.
- `page` e zero-based: primeira pagina e `page=0`.

### POST `/transactions`

Request receita:

```json
{
  "type": "INCOME",
  "accountId": "4c657ef7-b84d-452e-9e5e-75d5de410001",
  "categoryId": "2ef8b7cc-9e57-4a7a-9582-ff2609170002",
  "amount": 3000.00,
  "occurredAt": "2026-05-01",
  "description": "Salario",
  "note": null
}
```

Request despesa:

```json
{
  "type": "EXPENSE",
  "accountId": "4c657ef7-b84d-452e-9e5e-75d5de410001",
  "categoryId": "2ef8b7cc-9e57-4a7a-9582-ff2609170001",
  "amount": 85.90,
  "occurredAt": "2026-05-05",
  "description": "Mercado",
  "note": null
}
```

Response `201`: objeto de transacao.

Regra atual:

- Categoria `EXPENSE` so aceita transacao `EXPENSE`.
- Categoria `INCOME` so aceita transacao `INCOME`.
- Categoria `BOTH` aceita ambos.

### PUT `/transactions/{id}`

Request:

```json
{
  "accountId": "4c657ef7-b84d-452e-9e5e-75d5de410001",
  "categoryId": "2ef8b7cc-9e57-4a7a-9582-ff2609170001",
  "type": "EXPENSE",
  "amount": 90.00,
  "occurredAt": "2026-05-05",
  "description": "Mercado atualizado",
  "note": "Compra semanal",
  "active": true
}
```

Response `200`: objeto atualizado.

### DELETE `/transactions/{id}`

Response `204`.

Regra atual: soft delete com `active=false`.

### POST `/transactions/transfers`

Request:

```json
{
  "fromAccountId": "4c657ef7-b84d-452e-9e5e-75d5de410001",
  "toAccountId": "4c657ef7-b84d-452e-9e5e-75d5de410003",
  "amount": 500.00,
  "transactionDate": "2026-05-10",
  "description": "Reserva para investimento",
  "notes": null
}
```

Response `201`:

```json
{
  "id": "2bcb6ec7-16e4-4f1a-b387-350cf8690002",
  "type": "TRANSFER",
  "amount": 500.00,
  "transactionDate": "2026-05-10",
  "description": "Reserva para investimento",
  "account": {
    "id": "4c657ef7-b84d-452e-9e5e-75d5de410001",
    "name": "Conta Principal",
    "type": "CHECKING"
  },
  "transferAccount": {
    "id": "4c657ef7-b84d-452e-9e5e-75d5de410003",
    "name": "Investimentos",
    "type": "INVESTMENT"
  }
}
```

### POST `/transactions/installments`

Request:

```json
{
  "accountId": "4c657ef7-b84d-452e-9e5e-75d5de410002",
  "categoryId": "2ef8b7cc-9e57-4a7a-9582-ff2609170001",
  "totalAmount": 1000.00,
  "installmentTotal": 5,
  "firstTransactionDate": "2026-05-20",
  "firstBillingMonth": "2026-06",
  "description": "Compra parcelada",
  "merchantName": "Loja X",
  "notes": null
}
```

Response `201`:

```json
{
  "installmentGroupId": "725dce2d-936c-4767-b350-2fa8f8fa0001",
  "items": [
    {
      "id": "2bcb6ec7-16e4-4f1a-b387-350cf8690003",
      "amount": 200.00,
      "transactionDate": "2026-05-20",
      "billingMonth": "2026-06",
      "installmentNumber": 1,
      "installmentTotal": 5
    }
  ]
}
```

### GET `/transactions/card-summary?billingMonth=2026-06`

Response `200`:

```json
{
  "billingMonth": "2026-06",
  "total": 1320.00,
  "transactionsCount": 8,
  "byCategory": [
    {
      "categoryId": "2ef8b7cc-9e57-4a7a-9582-ff2609170001",
      "categoryName": "Alimentacao",
      "total": 420.00
    }
  ]
}
```

## Reports

### GET `/reports/monthly-summary?year=2026&month=5`

Retorna o resumo financeiro mensal considerando apenas transacoes ativas.

Query params:

```text
year=2026
month=5
```

Response `200`:

```json
{
  "income": 0,
  "expense": 0,
  "balance": 0
}
```

Regras:

- `income` soma transacoes `INCOME` ativas no mes.
- `expense` soma transacoes `EXPENSE` ativas no mes.
- `balance` e `income - expense`.
- Datas consideradas vao do primeiro ao ultimo dia do mes.
- Valores monetarios usam `BigDecimal` no backend.

## Budgets

### GET `/budgets/{month}`

Example: `/budgets/2026-05`

Response `200`:

```json
{
  "id": "631a843b-b7e5-4dac-b56b-f9a749cc0001",
  "month": "2026-05",
  "generalLimit": 4000.00,
  "categories": [
    {
      "categoryId": "2ef8b7cc-9e57-4a7a-9582-ff2609170001",
      "categoryName": "Alimentacao",
      "limitAmount": 1200.00
    }
  ]
}
```

### PUT `/budgets/{month}`

Request:

```json
{
  "generalLimit": 4000.00
}
```

Response `200`: objeto de budget.

### PUT `/budgets/{month}/categories/{categoryId}`

Request:

```json
{
  "limitAmount": 1200.00
}
```

Response `200`:

```json
{
  "categoryId": "2ef8b7cc-9e57-4a7a-9582-ff2609170001",
  "categoryName": "Alimentacao",
  "limitAmount": 1200.00
}
```

### DELETE `/budgets/{month}/categories/{categoryId}`

Response `204`.

### GET `/budgets/{month}/summary`

Response `200`:

```json
{
  "month": "2026-05",
  "generalLimit": 4000.00,
  "totalSpent": 2750.00,
  "generalUsagePercent": 68.75,
  "categories": [
    {
      "categoryId": "2ef8b7cc-9e57-4a7a-9582-ff2609170001",
      "categoryName": "Alimentacao",
      "limitAmount": 1200.00,
      "spentAmount": 950.00,
      "usagePercent": 79.17
    }
  ]
}
```

Regra: orcamento considera apenas despesas. Despesas de cartao entram pelo `billingMonth`.

## Goals

Enums:

```text
GoalStatus: ACTIVE, COMPLETED, ARCHIVED
```

### GET `/goals`

Response `200`:

```json
[
  {
    "id": "94b21033-34fa-47df-9c1f-3f6f70f10001",
    "name": "Reserva de emergencia",
    "targetAmount": 10000.00,
    "currentAmount": 2500.00,
    "targetDate": "2026-12-31",
    "status": "ACTIVE",
    "progressPercent": 25.00
  }
]
```

### POST `/goals`

Request:

```json
{
  "name": "Reserva de emergencia",
  "targetAmount": 10000.00,
  "currentAmount": 2500.00,
  "targetDate": "2026-12-31"
}
```

Response `201`: objeto de meta.

### PUT `/goals/{id}`

Request:

```json
{
  "name": "Reserva de emergencia",
  "targetAmount": 12000.00,
  "currentAmount": 3000.00,
  "targetDate": "2026-12-31",
  "status": "ACTIVE"
}
```

Response `200`: objeto atualizado.

### PATCH `/goals/{id}/progress`

Request:

```json
{
  "currentAmount": 3500.00
}
```

Response `200`: objeto atualizado.

### DELETE `/goals/{id}`

Response `204`.

Regra: arquiva a meta.

## Dashboard

### GET `/dashboard/monthly?month=2026-05`

Response `200`:

```json
{
  "month": "2026-05",
  "income": 5000.00,
  "expense": 2750.00,
  "result": 2250.00,
  "creditCardTotal": 1320.00,
  "budget": {
    "generalLimit": 4000.00,
    "spentAmount": 2750.00,
    "usagePercent": 68.75
  },
  "goals": [
    {
      "id": "94b21033-34fa-47df-9c1f-3f6f70f10001",
      "name": "Reserva de emergencia",
      "progressPercent": 25.00
    }
  ]
}
```

### GET `/dashboard/categories?month=2026-05`

Response `200`:

```json
[
  {
    "categoryId": "2ef8b7cc-9e57-4a7a-9582-ff2609170001",
    "categoryName": "Alimentacao",
    "total": 950.00,
    "percentage": 34.55
  }
]
```

### GET `/dashboard/cash-flow?from=2026-01&to=2026-05`

Response `200`:

```json
[
  {
    "month": "2026-01",
    "income": 5000.00,
    "expense": 3200.00,
    "result": 1800.00
  },
  {
    "month": "2026-02",
    "income": 5000.00,
    "expense": 2800.00,
    "result": 2200.00
  }
]
```

## Regras Criticas

- Toda consulta deve filtrar pelo usuario autenticado.
- Valores monetarios devem usar `BigDecimal` no Java.
- Valores monetarios nunca devem usar `double` ou `float`.
- `amount` deve ser sempre positivo.
- O sentido financeiro vem de `TransactionType`.
- Transacao deletada usa `deletedAt`, nao hard delete.
- Conta e categoria deletadas devem ser arquivadas.
- Despesa de cartao precisa de `billingMonth`.
- Dashboard mensal considera cartao por `billingMonth`.
- Orcamento considera apenas despesas.
- IA futura nao altera dados sem confirmacao do usuario.
- Importacao futura grava candidatos antes de confirmar transacoes finais.
