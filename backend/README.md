# Eco Backend

Backend planejado em Java com Spring Boot.

Este diretorio fica reservado para a implementacao manual do backend. A ideia e usar o projeto como treino real para vaga Jr backend Java, entao as regras de negocio, entidades, repositories, services e controllers devem ser implementados manualmente.

## Stack

- Java
- Spring Boot
- Spring Web
- Spring Security
- Spring Data JPA
- PostgreSQL
- Flyway
- Bean Validation
- Springdoc OpenAPI

## Estrutura Esperada

Quando o projeto Spring Boot for criado, a estrutura principal deve ficar assim:

```text
src/main/java/com/eco
  EcoApplication.java
  auth/
    controller/
    service/
    repository/
    dto/
    model/
  user/
    controller/
    service/
    repository/
    dto/
    model/
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
  budget/
    controller/
    service/
    repository/
    dto/
    model/
  goal/
    controller/
    service/
    repository/
    dto/
    model/
  dashboard/
    controller/
    service/
    dto/
  common/
    exception/
    response/
    validation/
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
user: eco_user
password: eco_password
```

Compilar sem rodar testes:

```powershell
.\mvnw.cmd -DskipTests compile
```

Rodar testes:

```powershell
.\mvnw.cmd test
```

Para `test` passar com a configuracao atual, o PostgreSQL precisa estar rodando via Docker.

## Ordem De Implementacao

Siga o backlog principal:

```text
../docs/BACKLOG.md
```

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
