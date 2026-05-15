# Eco Backend

Backend Java/Spring Boot do Eco.

Este projeto tambem funciona como trilha pratica para vaga Jr backend Java. As partes centrais do backend sao feitas manualmente, com IA apoiando revisao, testes, organizacao e explicacoes.

## Stack

- Java 21
- Spring Boot 3.5.14
- Spring Web
- Spring Security
- Spring Data JPA
- PostgreSQL
- Flyway
- Bean Validation
- Springdoc OpenAPI
- JUnit, Mockito e AssertJ

## Estrutura Atual

```text
src/main/java/com/eco
  EcoBackendApplication.java
  account/
    controller/
    service/
    repository/
    dto/
    model/
  category/
    controller/
    service/
    repository/
    dto/
    model/
  transaction/
    controller/
    service/
    repository/
    dto/
    model/
  report/
    controller/
    service/
    dto/
  common/
    exception/
  config/
```

## Ambiente Local

Verificar Java:

```powershell
java -version
javac -version
echo $env:JAVA_HOME
```

Esperado:

```text
Java 21
javac 21
JAVA_HOME apontando para o JDK 21
```

Se o terminal ainda mostrar Java 8, feche e abra o terminal depois de configurar `JAVA_HOME`.

Subir PostgreSQL:

```bash
docker compose up -d
```

Parar PostgreSQL:

```bash
docker compose down
```

Ver logs:

```bash
docker compose logs -f postgres
```

## Banco Local

Valores locais do `docker-compose.yml`:

```text
host: localhost
port: 5432
database: eco
user: eco_local
password: eco_local_change_me
```

O Postgres local fica bindado em `127.0.0.1`, nao em todas as interfaces da maquina.
Troque `POSTGRES_PASSWORD` no seu `.env` se a maquina for compartilhada.

Configuracoes sensiveis podem ser sobrescritas por variaveis de ambiente:

```text
SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD
ECO_JWT_SECRET
ECO_ACCESS_TOKEN_SECONDS
ECO_REFRESH_TOKEN_SECONDS
ECO_COOKIE_SECURE
```

Fora do profile `dev`, `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`,
`SPRING_DATASOURCE_PASSWORD` e `ECO_JWT_SECRET` sao obrigatorios. Se algum deles
nao existir, a aplicacao deve falhar ao iniciar.

Para rodar local com defaults de estudo:

```powershell
$env:SPRING_PROFILES_ACTIVE='dev'
.\mvnw.cmd spring-boot:run
```

Sem `.env`, o profile `dev` usa os mesmos defaults do `docker-compose.yml`:
`eco_local` / `eco_local_change_me`.

O usuario `dev@eco.com` e seedado apenas pelo profile `dev`, via migrations em
`src/main/resources/db/dev-migration`.

Compilar sem rodar testes:

```powershell
.\mvnw.cmd -DskipTests compile
```

Rodar testes:

```powershell
.\mvnw.cmd test
```

Para `test` passar com a configuracao atual, o PostgreSQL precisa estar acessivel em `localhost:5432/eco`.
Use o profile `dev` ou exporte as variaveis obrigatorias antes do teste.

Se `JAVA_HOME` nao estiver configurado globalmente:

```cmd
set JAVA_HOME=C:\Program Files\Java\jdk-21.0.11&& set PATH=C:\Program Files\Java\jdk-21.0.11\bin;%PATH%&& .\mvnw.cmd test
```

## Endpoints Implementados

Base local:

```text
http://localhost:8080/api
```

Implementado:

- `POST /auth/login`
- `POST /auth/refresh`
- `POST /auth/logout`
- `GET /auth/me`
- `GET /categories`
- `GET /categories/{id}`
- `POST /categories`
- `PUT /categories/{id}`
- `DELETE /categories/{id}`
- `GET /accounts`
- `GET /accounts/{id}`
- `POST /accounts`
- `PUT /accounts/{id}`
- `DELETE /accounts/{id}`
- `GET /transactions` com filtros e paginacao
- `GET /transactions/{id}`
- `POST /transactions`
- `PUT /transactions/{id}`
- `DELETE /transactions/{id}`
- `POST /transactions/transfers`
- `POST /transactions/installments`
- `GET /transactions/card-summary?billingMonth=2026-05`
- `GET /budgets/{month}`
- `PUT /budgets/{month}`
- `PUT /budgets/{month}/categories/{categoryId}`
- `DELETE /budgets/{month}/categories/{categoryId}`
- `GET /budgets/{month}/summary`
- `GET /goals`
- `POST /goals`
- `GET /goals/{id}`
- `PUT /goals/{id}`
- `PATCH /goals/{id}/progress`
- `DELETE /goals/{id}`
- `GET /dashboard/monthly?month=2026-05`
- `GET /dashboard/categories?month=2026-05`
- `GET /dashboard/cash-flow?months=6`
- `GET /reports/monthly-summary?year=2026&month=5`

## Testes

Rodar a suite completa antes de publicar:

```powershell
.\mvnw.cmd test
```

## CORS

Configurado em `SecurityConfig` para permitir o frontend local:

```text
http://localhost:3000
```

Metodos liberados:

```text
GET, POST, PUT, DELETE, OPTIONS
```

Contrato de API:

```text
../docs/API_CONTRACT.md
```

Escopo tecnico:

```text
../docs/MVP_SCOPE.md
```

## Publicacao

Antes de publicar uma instancia real, rode sem profile `dev`, configure `ECO_JWT_SECRET`
com valor forte, configure credenciais reais do banco, mantenha `ECO_COOKIE_SECURE=true`
em HTTPS e revise CORS.
