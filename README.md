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
  docs/
    MVP_SCOPE.md
    BACKLOG.md
    API_CONTRACT.md
  frontend/
    public/
    src/
    package.json
    README.md
```

O backend esta sendo implementado manualmente em Java/Spring Boot. O frontend ja possui login, dashboard e gerenciamento de transacoes integrados com a API real, mantendo fallback mockado para demonstracao quando o backend esta desligado.

## Stack

### Backend

- Java 21
- Spring Boot 3.5.14
- Spring Web
- Spring Security com JWT nos endpoints privados
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

- Next.js 16
- React 19
- CSS puro
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
- `/api/auth`
- `/api/budgets`
- `/api/goals`
- `/api/dashboard`

O contrato inicial da API esta em [API_CONTRACT.md](./docs/API_CONTRACT.md).

## Status Atual

MVP funcional local concluido.

Backend:

- migrations Flyway para `users`, `refresh_tokens`, `categories`, `accounts`, `transactions`, `monthly_budgets`, `category_budgets` e `goals`;
- seed local de usuario de desenvolvimento;
- auth JWT com login, refresh, logout e `/auth/me`;
- filtro JWT conectado ao Spring Security;
- endpoints privados protegidos por Bearer token;
- CRUD de categorias;
- CRUD de contas;
- CRUD de transacoes;
- isolamento dos dados financeiros por usuario autenticado (`user_id`);
- filtros em `GET /api/transactions`;
- paginacao em `GET /api/transactions`;
- regra de compatibilidade entre tipo da categoria e tipo da transacao;
- transferencias entre contas;
- compras parceladas;
- resumo de cartao por `billingMonth`;
- budgets mensais e por categoria;
- goals com progresso manual;
- dashboard completo em `/api/dashboard`;
- resumo mensal em `GET /api/reports/monthly-summary`;
- tratamento global de erros;
- tratamento de erro `401 Unauthorized` para requests sem autenticacao;
- testes unitarios de services;
- testes de controller com `@WebMvcTest`;
- teste unitario do filtro JWT;
- CI configurado para rodar testes.

Frontend:

- tela `/login`;
- armazenamento local de `accessToken` e `refreshToken`;
- client HTTP com header `Authorization: Bearer <accessToken>`;
- tentativa de refresh token quando a API retorna `401`;
- redirect para `/login` quando a sessao expira;
- logout basico;
- Next.js com dashboard integrado ao backend;
- client HTTP em `frontend/src/lib/api.ts`;
- fallback para mocks quando o backend esta desligado;
- criacao de transacao pelo dashboard;
- tela `/transactions` com filtros, paginacao, criacao, edicao e exclusao;
- telas `/budgets` e `/goals` lendo API real com fallback mockado;
- criacao/atualizacao simples de budgets e goals pelo frontend;
- PWA base com manifest, tema mobile e icone.

A ordem sugerida de implementacao esta em [BACKLOG.md](./docs/BACKLOG.md).

## Rodar Localmente

### Backend

Subir PostgreSQL:

```powershell
cd backend
docker compose up -d
```

Rodar Spring Boot:

```powershell
.\mvnw.cmd spring-boot:run
```

API local:

```text
http://localhost:8080/api
```

Swagger:

```text
http://localhost:8080/swagger-ui/index.html
```

### Frontend

Instalar dependencias e rodar:

```powershell
cd frontend
npm install
npm run dev
```

App local:

```text
http://localhost:3000
```

Se precisar trocar a URL da API, crie `frontend/.env.local`:

```text
NEXT_PUBLIC_API_BASE_URL=http://localhost:8080/api
```

## Usuario Local

O banco local cria um usuario de desenvolvimento via migration. Para uso real ou deploy publico, remova esse seed e configure usuarios proprios.

## Validacao Local

Backend:

```powershell
cd backend
$env:JAVA_HOME='C:\Program Files\Java\jdk-21.0.11'
.\mvnw.cmd test
```

Frontend:

```powershell
cd frontend
npm run build
```

## Versao Demonstravel

O projeto possui uma versao MVP funcional full stack para portfolio:

- cadastro e listagem de contas/categorias via backend;
- CRUD de transacoes;
- transferencias;
- parcelamento;
- cartao por fatura;
- budgets;
- goals;
- dashboard mensal completo;
- filtros e paginacao de transacoes;
- frontend responsivo com PWA base;
- login frontend integrado ao backend;
- refresh token no client HTTP;
- testes backend e build frontend.

Antes de divulgar uma instancia publica, revise o seed local, configure variaveis de ambiente reais e troque o segredo JWT.

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
- [CONTRIBUTING.md](./CONTRIBUTING.md)
- [SECURITY.md](./SECURITY.md)
- [LICENSE](./LICENSE)
