# Prompt Unico Para Kimi - Frontend/Design Eco

Voce vai trabalhar no projeto Eco em `D:\projetos\Eco`, uma PWA de financas pessoais para portfolio de vaga Jr.

## Contexto

Backend:

- Java 21, Spring Boot 3.5.14, PostgreSQL, Flyway, JPA.
- Security temporariamente liberado.
- Base local: `http://localhost:8080/api`.
- Endpoints atuais: contas, categorias, transacoes, resumo mensal.
- Contrato: leia `docs/API_CONTRACT.md`.

Frontend:

- Next.js 16, React 19, TypeScript, CSS puro.
- Arquivos principais:
  - `frontend/src/app/page.tsx`
  - `frontend/src/app/transactions/page.tsx`
  - `frontend/src/components/TransactionModal.tsx`
  - `frontend/src/lib/api.ts`
  - `frontend/src/app/globals.css`
- Ja existe:
  - dashboard integrado ao backend;
  - fallback mockado no dashboard se backend estiver desligado;
  - criacao de transacao;
  - tela `/transactions` com filtros, paginacao, edicao e exclusao;
  - PWA base com manifest e icone.

## Objetivo

Melhorar somente frontend/design sem exigir novos endpoints backend.

Nao implementar backend. Nao mudar contrato da API sem necessidade.

## Tarefas

1. Refinar UX mobile-first:
   - revisar `/` e `/transactions` em larguras pequenas;
   - garantir que filtros, botoes, valores monetarios e acoes nao quebrem layout;
   - manter interface simples, profissional e adequada a app financeiro.

2. Melhorar visual de portfolio:
   - dar acabamento consistente em espacamentos, estados vazios, loading e erros;
   - manter paleta sobria, sem hero marketing, sem cards aninhados;
   - preservar CSS puro.

3. Melhorar acessibilidade:
   - labels claros;
   - foco visivel;
   - botoes com `aria-label` quando forem icon-only;
   - modal com comportamento acessivel razoavel.

4. Melhorar PWA base se couber sem dependencia extra:
   - revisar manifest;
   - revisar metadata;
   - manter build funcionando.

5. Documentar brevemente no `frontend/README.md` qualquer decisao relevante.

## Restrições

- Nao adicionar biblioteca de UI.
- Nao trocar stack.
- Nao criar landing page.
- Nao mexer em backend.
- Nao remover fallback mockado do dashboard.
- Nao quebrar endpoints atuais.
- Rodar `npm run build` no final.

## Criterios De Aceite

- `npm run build` passa.
- `/` continua carregando resumo e ultimas transacoes.
- `/transactions` continua criando, editando, excluindo, filtrando e paginando.
- Layout responsivo funciona bem em mobile e desktop.
- Codigo segue o estilo atual do projeto.
