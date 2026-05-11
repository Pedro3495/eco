# Contributing

Eco e um projeto de portfolio e estudo. Contribuicoes sao bem-vindas, mas devem manter o escopo simples e explicavel.

## Como Rodar

Backend:

```powershell
cd backend
docker compose up -d
.\mvnw.cmd test
```

Frontend:

```powershell
cd frontend
npm install
npm run build
```

## Padrao Esperado

- Use Java 21 no backend.
- Use migrations Flyway para mudancas de banco.
- Nao exponha entities diretamente na API.
- Mantenha dados financeiros filtrados por usuario autenticado.
- Adicione ou atualize testes quando mexer em regra de negocio.
- Atualize `docs/API_CONTRACT.md` quando mudar contrato HTTP.

## Commits

Use mensagens objetivas. Exemplo:

```text
feat: add goals endpoints
fix: refresh token retry in frontend api client
docs: update mvp setup instructions
```

