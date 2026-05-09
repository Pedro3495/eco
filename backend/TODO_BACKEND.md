# TODO Backend Manual

Use este arquivo como trilho de estudo. Marque cada item conforme implementar.

## 1. Criacao Do Projeto

- [ ] Criar projeto Spring Boot.
- [ ] Package base: `com.eco`.
- [ ] Adicionar dependencias:
  - Spring Web
  - Spring Data JPA
  - Spring Security
  - PostgreSQL Driver
  - Flyway
  - Validation
  - Springdoc OpenAPI
- [ ] Configurar `application.yml`.
- [ ] Rodar aplicacao vazia.
- [ ] Abrir Swagger local.

## 2. Docker E Banco

- [ ] Subir PostgreSQL com Docker Compose.
- [ ] Conectar aplicacao ao banco.
- [ ] Criar primeira migration Flyway.
- [ ] Confirmar que migration roda ao iniciar.

## 3. Common

- [ ] Criar padrao de erro.
- [ ] Criar `@RestControllerAdvice`.
- [ ] Criar excecoes customizadas.
- [ ] Criar padrao de auditoria.

## 4. Auth

- [ ] Criar `User`.
- [ ] Criar `RefreshToken`.
- [ ] Implementar BCrypt.
- [ ] Implementar login.
- [ ] Implementar refresh.
- [ ] Implementar logout.
- [ ] Implementar `/auth/me`.
- [ ] Proteger endpoints.

## 5. Modulos De Dominio

- [ ] `Account`.
- [ ] `Category`.
- [ ] `Transaction`.
- [ ] `Budget`.
- [ ] `Goal`.
- [ ] `Dashboard`.

## 6. Testes

- [ ] Testar services principais.
- [ ] Testar repositories importantes.
- [ ] Testar auth.
- [ ] Testar parcelamento.
- [ ] Testar transferencia.

