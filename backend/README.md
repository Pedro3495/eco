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

Valores padrao do `docker-compose.yml`:

```text
host: localhost
port: 5432
database: eco
user: postgres
password: postgres
```

Compilar sem rodar testes:

```powershell
.\mvnw.cmd -DskipTests compile
```

Rodar testes:

```powershell
.\mvnw.cmd test
```

Para `test` passar com a configuracao atual, o PostgreSQL precisa estar acessivel em `localhost:5432/eco`.

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
- `GET /reports/monthly-summary?year=2026&month=5`

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

## CORS

Configurado em `SecurityConfig` para permitir o frontend local:

```text
http://localhost:3000
```

Metodos liberados:

```text
GET, POST, PUT, DELETE, OPTIONS
```

## Proximas Etapas Backend

- integrar frontend com Kimi/opencode usando `docs/FRONTEND_HANDOFF_KIMI.md`;
- depois implementar auth/JWT, budgets, goals e features avancadas.

Contrato de API:

```text
../docs/API_CONTRACT.md
```

Escopo tecnico:

```text
../docs/MVP_SCOPE.md
```

## O Que A IA Nao Deve Fazer Aqui

- Implementar entidades por voce sem pedido explicito.
- Implementar services completos por voce sem revisao.
- Escrever regras de negocio centrais no seu lugar.

## Como Usar A IA Aqui

Use a IA para:

- revisar seu codigo;
- explicar erros;
- sugerir testes;
- ajudar a debugar;
- comparar alternativas;
- apontar melhorias de portfolio;
- explicar conceitos de Spring Boot.
