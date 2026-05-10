# Eco - Handoff Para Frontend Com Kimi/opencode

Use este arquivo como contexto rapido para integrar o frontend Next.js ao backend Spring Boot.

## Estado Atual

Backend:

- Base URL local: `http://localhost:8080/api`
- Spring Security esta temporariamente liberado com `permitAll`.
- Ainda nao ha JWT/login real.
- PostgreSQL local precisa estar rodando.
- API retorna JSON.

Frontend:

- Next.js em `frontend/`
- Tela atual usa mocks em `frontend/src/mocks/finance-data.ts`
- Primeiro objetivo: substituir mocks por chamadas HTTP reais, sem redesenhar a UI inteira.

## Endpoints Prontos Para Integracao

### Resumo Mensal

```text
GET http://localhost:8080/api/reports/monthly-summary?year=2026&month=5
```

Resposta real testada:

```json
{
  "income": 0,
  "expense": 0,
  "balance": 0
}
```

Observacao: zerado porque a transacao existente estava `active=false`.

### Transacoes Paginadas

```text
GET http://localhost:8080/api/transactions?page=0&size=10
```

Tambem aceita filtros:

```text
GET http://localhost:8080/api/transactions?active=false&page=0&size=10
GET http://localhost:8080/api/transactions?type=EXPENSE&page=0&size=10
GET http://localhost:8080/api/transactions?startDate=2026-05-01&endDate=2026-05-31&page=0&size=10
```

Formato:

```json
{
  "items": [
    {
      "id": "uuid",
      "description": "Mercado",
      "amount": 75.10,
      "type": "EXPENSE",
      "occurredAt": "2026-05-09",
      "accountId": "uuid",
      "accountName": "Conta Principal",
      "categoryId": "uuid",
      "categoryName": "Alimentacao",
      "note": "Compra semanal",
      "active": false
    }
  ],
  "page": 0,
  "size": 10,
  "totalItems": 1,
  "totalPages": 1
}
```

### Categorias

```text
GET http://localhost:8080/api/categories
```

Formato:

```json
[
  {
    "id": "uuid",
    "name": "Alimentacao",
    "kind": "EXPENSE",
    "color": "#E86F51",
    "icon": "utensils",
    "active": true
  }
]
```

### Contas

```text
GET http://localhost:8080/api/accounts
```

Formato:

```json
[
  {
    "id": "uuid",
    "name": "Conta Principal",
    "type": "CHECKING",
    "initialBalance": 0,
    "active": true
  }
]
```

## Primeira Integracao Recomendada

1. Criar `frontend/src/lib/api.ts`.
2. Definir `API_BASE_URL`, preferencialmente via env:

```text
NEXT_PUBLIC_API_BASE_URL=http://localhost:8080/api
```

3. Criar funcoes:

```text
getMonthlySummary(year, month)
getTransactions({ page, size, active })
getCategories()
getAccounts()
```

4. Trocar no dashboard:

- `monthlySummary` mock -> `/reports/monthly-summary`
- `transactions` mock -> `/transactions?page=0&size=10`

5. Adicionar estados simples:

- loading
- erro
- lista vazia

## CORS

Backend ja configurado para permitir:

```text
http://localhost:3000
```

Metodos liberados:

```text
GET, POST, PUT, DELETE, OPTIONS
```

## Cuidados

- Nao implementar login/JWT ainda no frontend.
- Nao redesenhar a UI inteira antes de conectar os dados.
- Manter mocks como fallback temporario se quiser.
- O campo de resultado no frontend mockado chama `result`, mas a API retorna `balance`.
- A data da transacao na API chama `occurredAt`.
- Categoria/conta em transacao vêm como `categoryName` e `accountName`.
