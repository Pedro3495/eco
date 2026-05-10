# TODO Backend Manual

Use este arquivo como trilho de estudo. Marque cada item conforme implementar.

## 1. Criacao Do Projeto

- [x] Criar projeto Spring Boot.
- [x] Package base: `com.eco`.
- [x] Adicionar dependencias:
  - Spring Web
  - Spring Data JPA
  - Spring Security
  - PostgreSQL Driver
  - Flyway
  - Validation
  - Springdoc OpenAPI
- [x] Configurar `application.yaml`.
- [x] Rodar aplicacao.
- [x] Abrir Swagger local.

## 2. Docker E Banco

- [x] Subir PostgreSQL com Docker Compose/local.
- [x] Conectar aplicacao ao banco.
- [x] Criar migrations Flyway para categorias, contas e transacoes.
- [x] Confirmar que migrations rodam ao iniciar.

## 3. Common

- [x] Criar padrao de erro.
- [x] Criar `@RestControllerAdvice`.
- [x] Criar excecoes customizadas.
- [x] Criar campos basicos de auditoria nas entidades atuais.

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

- [x] `Account`.
- [x] `Category`.
- [x] `Transaction`.
- [x] `Report` mensal simples.
- [ ] `Budget`.
- [ ] `Goal`.
- [ ] `Dashboard`.

## 6. Testes

- [x] Testar services principais.
- [x] Testar primeiro controller com `@WebMvcTest`.
- [ ] Testar repositories importantes.
- [ ] Testar auth.
- [ ] Testar parcelamento.
- [ ] Testar transferencia.

## 7. Implementado Recentemente

- [x] Filtros em `GET /transactions`: `accountId`, `categoryId`, `type`, `startDate`, `endDate`, `active`.
- [x] Paginacao em `GET /transactions`: `page`, `size`, `sort`.
- [x] Regra categoria x transacao.
- [x] `GET /reports/monthly-summary`.
- [x] Documentacao em `docs/JR_BACKEND_NOTES.md`.

## 8. Proximo

- [x] Configurar CORS para o frontend.
- [x] Preparar handoff para integracao frontend no opencode/Kimi.
