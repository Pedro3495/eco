# Eco Backend - Registro De Aprendizado

Este arquivo resume o que foi feito ate agora no backend, o motivo de cada etapa e o proximo passo recomendado.

## Estado Atual

Projeto Spring Boot criado em:

```text
D:\projetos\Eco\backend
```

Stack atual:

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

O projeto ja compila e o teste inicial ja passou depois da configuracao correta de Java, banco e Flyway.

## Configuracoes Feitas

### Java

O projeto usa Java 21.

Foi necessario configurar o JDK, nao apenas JRE.

Comandos uteis:

```powershell
java -version
javac -version
echo $env:JAVA_HOME
```

Esperado:

```text
java 21
javac 21
JAVA_HOME apontando para C:\Program Files\Java\jdk-21.0.11
```

Problema encontrado:

```text
No compiler is provided in this environment. Perhaps you are running on a JRE rather than a JDK?
```

Causa:

O Maven estava usando Java 8/JRE em vez de JDK 21.

Licao:

Spring Boot com Java 21 precisa que o terminal e o Maven usem JDK 21.

## Docker E PostgreSQL

Arquivo:

```text
backend/docker-compose.yml
```

Servico configurado:

```yaml
services:
  postgres:
    image: postgres:16-alpine
    container_name: eco-postgres
    restart: unless-stopped
    environment:
      POSTGRES_DB: eco
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - eco_postgres_data:/var/lib/postgresql/data
```

Comandos importantes:

```powershell
docker compose up -d
docker ps
docker compose logs -f postgres
docker compose down
docker compose down -v
```

Observacao:

`docker compose down -v` apaga o volume e, portanto, apaga os dados locais do banco. Usar com cuidado.

Problemas encontrados:

- Docker Desktop nao estava rodando.
- Senha do PostgreSQL estava inconsistente.
- Banco `eco` nao existia.
- Havia indicio de PostgreSQL local na porta `5432`, alem do container.

Licao:

Spring Boot conecta no que estiver em `localhost:5432`. Se houver PostgreSQL local e container usando a mesma porta, pode haver confusao. O importante e manter `application.yaml`, Docker e banco real consistentes.

## Configuracao Spring

Arquivo:

```text
src/main/resources/application.yaml
```

Configuracao atual:

```yaml
spring:
  application:
    name: eco-backend

  datasource:
    url: jdbc:postgresql://localhost:5432/eco
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: validate
    open-in-view: false
    properties:
      hibernate:
        format_sql: true

  flyway:
    enabled: true

server:
  servlet:
    context-path: /api
```

Pontos importantes:

- `ddl-auto: validate` faz o Hibernate validar se as entidades batem com as tabelas.
- O Hibernate nao cria tabelas automaticamente.
- O Flyway e responsavel por criar/alterar tabelas.
- `open-in-view: false` evita manter sessao JPA aberta na camada web.

## Flyway

Foi criada a primeira migration:

```text
src/main/resources/db/migration/V1__create_categories_table.sql
```

Conteudo:

```sql
CREATE TABLE categories (
    id UUID PRIMARY KEY,
    name VARCHAR(80) NOT NULL,
    kind VARCHAR(20) NOT NULL,
    color VARCHAR(20),
    icon VARCHAR(50),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

ALTER TABLE categories
    ADD CONSTRAINT uk_categories_name UNIQUE (name);
```

Regras aprendidas:

- Migration precisa ficar em `src/main/resources/db/migration`.
- Nome precisa seguir o padrao `V1__descricao.sql`.
- Sao dois underscores entre versao e descricao.
- Depois que uma migration roda, nao editar como se fosse rascunho.
- Para mudar o banco depois, criar nova migration `V2__...sql`.

Problema encontrado:

Hibernate reclamou quando a entity nao batia com a migration:

```text
wrong column type encountered in column [id]
found [uuid], but expecting [varchar(255)]
```

Causa:

No banco, `id` era `UUID`. Na entity, `id` estava como `String`.

Licao:

Com `ddl-auto: validate`, o Hibernate ajuda a encontrar divergencia entre Java e banco.

## Modulo Category

Estrutura criada:

```text
src/main/java/com/eco/category
  model/
    Category.java
    CategoryKind.java
  repository/
    CategoryRepository.java
  dto/
    CreateCategoryRequest.java
    UpdateCategoryRequest.java
    CategoryResponse.java
```

## Entity

Arquivo:

```text
src/main/java/com/eco/category/model/Category.java
```

Responsabilidade:

Representar a tabela `categories` no Java.

Conceito:

```text
Entity = modelo persistido no banco
```

Pontos importantes:

- Usa `@Entity`.
- Usa `@Table(name = "categories")`.
- O `id` deve ser `UUID`, porque no banco e `UUID`.
- Campos em snake_case no banco precisam de `@Column(name = "...")` quando o nome Java e camelCase.
- O construtor vazio `protected Category() {}` existe para o Hibernate.
- O construtor publico deve criar uma categoria valida.

Construtor recomendado:

```java
public Category(String name, CategoryKind kind, String color, String icon) {
    this.id = UUID.randomUUID();
    this.name = name;
    this.kind = kind;
    this.color = color;
    this.icon = icon;
    this.active = true;
    this.createdAt = Instant.now();
    this.updatedAt = Instant.now();
}
```

Licao sobre o construtor vazio:

```java
protected Category() {}
```

Ele existe para o Hibernate conseguir criar objetos ao ler dados do banco. Usar `protected` evita que o resto do codigo crie entidades vazias sem necessidade.

## Enum

Arquivo:

```text
src/main/java/com/eco/category/model/CategoryKind.java
```

Conteudo:

```java
public enum CategoryKind {
    INCOME,
    EXPENSE,
    BOTH
}
```

Responsabilidade:

Limitar os tipos possiveis de categoria.

No banco, o enum e salvo como texto por causa de:

```java
@Enumerated(EnumType.STRING)
```

## Repository

Arquivo:

```text
src/main/java/com/eco/category/repository/CategoryRepository.java
```

Conteudo esperado:

```java
package com.eco.category.repository;

import com.eco.category.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    Optional<Category> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}
```

Responsabilidade:

Acessar o banco de dados.

Conceito:

```text
Repository = acesso ao banco
```

O `JpaRepository` ja fornece:

- `save`
- `findById`
- `findAll`
- `delete`
- `deleteById`
- `existsById`
- `count`

Metodos customizados criados:

- `findByNameIgnoreCase`
- `existsByNameIgnoreCase`

Licao:

Repository deve ser interface, nao classe.

Errado:

```java
public class CategoryRepository {
}
```

Certo:

```java
public interface CategoryRepository extends JpaRepository<Category, UUID> {
}
```

## DTOs

DTO significa:

```text
Data Transfer Object
```

Responsabilidade:

Transportar dados de entrada e saida da API.

Fluxo:

```text
JSON da requisicao -> DTO Request -> Service -> Entity -> Banco
Banco -> Entity -> DTO Response -> JSON da resposta
```

Regra mental:

```text
Entity = banco
Request DTO = entrada da API
Response DTO = saida da API
```

Motivo para usar DTO:

- Evita expor entity diretamente.
- Evita acoplar API ao banco.
- Permite controlar exatamente o que entra e o que sai.
- Facilita validacao.
- Evita problemas futuros com relacionamentos JPA e serializacao JSON.

### CreateCategoryRequest

Arquivo:

```text
src/main/java/com/eco/category/dto/CreateCategoryRequest.java
```

Responsabilidade:

Receber os dados para criar uma categoria.

Sera usado no endpoint:

```text
POST /categories
```

Exemplo de JSON:

```json
{
  "name": "Alimentacao",
  "kind": "EXPENSE",
  "color": "#E86F51",
  "icon": "utensils"
}
```

Validacoes importantes:

- `@NotBlank` para nome.
- `@NotNull` para tipo.
- `@Size` para limitar tamanho.

### UpdateCategoryRequest

Arquivo:

```text
src/main/java/com/eco/category/dto/UpdateCategoryRequest.java
```

Responsabilidade:

Receber os dados para editar uma categoria.

Sera usado no endpoint:

```text
PUT /categories/{id}
```

Exemplo de JSON:

```json
{
  "name": "Mercado",
  "kind": "EXPENSE",
  "color": "#E86F51",
  "icon": "shopping-cart",
  "active": true
}
```

Diferença em relacao ao create:

Inclui `active`, porque uma edicao completa pode ativar/desativar a categoria.

### CategoryResponse

Arquivo:

```text
src/main/java/com/eco/category/dto/CategoryResponse.java
```

Responsabilidade:

Devolver dados da categoria na resposta da API.

Sera usado em endpoints como:

```text
GET /categories
GET /categories/{id}
POST /categories
PUT /categories/{id}
```

Exemplo de JSON:

```json
{
  "id": "uuid",
  "name": "Alimentacao",
  "kind": "EXPENSE",
  "color": "#E86F51",
  "icon": "utensils",
  "active": true
}
```

Metodo importante:

```java
public static CategoryResponse fromEntity(Category category)
```

Responsabilidade desse metodo:

Converter uma entity `Category` em um DTO `CategoryResponse`.

## Camadas Aprendidas Ate Agora

```text
Controller = recebe HTTP e devolve HTTP
Service = regra de negocio
Repository = acesso ao banco
Entity = tabela/registro do banco
DTO = contrato da API
Migration = versao do banco
```

Ainda faltam no modulo Category:

- `CategoryService`
- `CategoryController`
- tratamento de erros especificos
- testes do service

## Comandos Uteis

Rodar testes:

```powershell
.\mvnw.cmd test
```

Compilar sem testes:

```powershell
.\mvnw.cmd -DskipTests compile
```

Subir PostgreSQL:

```powershell
docker compose up -d
```

Ver containers:

```powershell
docker ps
```

Entrar no PostgreSQL:

```powershell
psql -h localhost -p 5432 -U postgres -d eco
```

Listar tabelas no `psql`:

```sql
\dt
```

Ver estrutura da tabela:

```sql
\d categories
```

Sair do `psql`:

```sql
\q
```

## Proxima Etapa Recomendada

Criar o service:

```text
src/main/java/com/eco/category/service/CategoryService.java
```

Responsabilidades iniciais do service:

- listar categorias;
- buscar categoria por id;
- criar categoria;
- impedir nome duplicado;
- atualizar categoria;
- arquivar/desativar categoria.

Depois criar:

```text
src/main/java/com/eco/category/controller/CategoryController.java
```

Endpoints planejados:

```text
GET /categories
POST /categories
GET /categories/{id}
PUT /categories/{id}
DELETE /categories/{id}
```

