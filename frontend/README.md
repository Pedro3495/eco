# Eco Frontend

Frontend em Next.js do Eco, um PWA pessoal de controle financeiro.

## Stack

- Next.js 16
- React 19
- TypeScript
- CSS puro
- Lucide React

## Rodar Localmente

Instalar dependências:

```powershell
npm install
```

Rodar em desenvolvimento:

```powershell
npm run dev
```

Abrir:

```text
http://localhost:3000
```

Por padrão, o frontend usa:

```text
http://localhost:8080/api
```

Para alterar, crie `.env.local`:

```text
NEXT_PUBLIC_API_BASE_URL=http://localhost:8080/api
```

## Backend Necessário

Antes de usar os fluxos reais:

```powershell
cd ..\backend
docker compose up -d
.\mvnw.cmd spring-boot:run
```

## Funcionalidades Atuais

- Dashboard integrado ao backend.
- Resumo mensal via `GET /reports/monthly-summary`.
- Últimas transações via `GET /transactions`.
- Criação de transação via `POST /transactions`.
- Tela `/transactions` com filtros por período, tipo, conta e categoria.
- Edição via `PUT /transactions/{id}`.
- Exclusão via `DELETE /transactions/{id}`.
- Fallback para mocks quando a API está indisponível no dashboard.
- PWA base com manifest, tema mobile e ícone.

## Estrutura Relevante

```text
src/app/page.tsx
src/app/transactions/page.tsx
src/components/TransactionModal.tsx
src/lib/api.ts
src/lib/format.ts
src/mocks/finance-data.ts
```

## Scripts

Build de produção:

```powershell
npm run build
```

Rodar produção local após build:

```powershell
npm run start
```

## Pendências Frontend

- Tela de login quando auth/JWT existir no backend.
- Client HTTP autenticado com Bearer token e refresh token.
- Telas dedicadas de contas e categorias.
- Telas de orçamentos e metas quando os endpoints existirem.
- Service worker/offline cache mais completo.
- Refinos visuais para portfolio, screenshots e responsividade final.

## Decisões de Design e UX

### Mobile-first

O layout foi pensado para funcionar bem em telas pequenas antes de desktop:

- O shell reduz padding lateral em mobile (`16px` vs `24px`).
- O topbar mantém a marca e ações em uma única linha quando possível; em telas muito pequenas (`<380px`) os botões ocupam a largura total.
- A grid de métricas no dashboard passa de 3 colunas para 1 coluna em mobile.
- A lista de transações em mobile reorganiza a descrição em linha única, valor à esquerda e ações à direita, evitando overflow de textos longos.
- Filtros em `/transactions` usam 5 colunas em desktop, 3 em tablet (`<860px`) e 1 coluna em mobile (`<640px`), com botões de ação empilhados em telas pequenas.
- Paginação empilha botões em mobile para não haver overflow horizontal.

### Modal Acessível

O modal de transação implementa padrões de acessibilidade:

- Fecha ao pressionar `ESC` (se não estiver salvando).
- Fecha ao clicar no backdrop (se não estiver salvando).
- Foco é movido para o primeiro campo ao abrir.
- Foco é retornado ao elemento que abriu o modal ao fechar.
- Trap de foco: `Tab` e `Shift+Tab` circulam apenas entre elementos do modal.
- Scroll do body é bloqueado enquanto o modal está aberto.
- `aria-labelledby`, `aria-describedby` e `role="dialog"` são usados.

### Acessibilidade Geral

- Skip link permite pular para o conteúdo principal.
- Todos os botões `icon-only` possuem `aria-label` descritivo (inclusive com o nome da transação nos botões de editar/excluir).
- Ícones decorativos usam `aria-hidden="true"`.
- Estados de loading usam `aria-busy="true"` e `aria-live="polite"`.
- Mensagens de erro usam `role="alert"`.
- Inputs de formulário possuem `htmlFor`/`id` associados.
- Foco visível usa `box-shadow` azul semitransparente e `outline: none` apenas quando o foco é visível via `:focus-visible`.
- Animações respeitam `prefers-reduced-motion`.

### PWA

- Manifesto inclui `categories`, `lang` e ícones com purpose `any` e `maskable`.
- Viewport permite zoom (`maximum-scale: 5`, `user-scalable: true`) para acessibilidade.
- Metadata inclui `formatDetection: { telephone: false }` para evitar links automáticos de telefone.

### CSS Puro

- Nenhuma biblioteca de UI foi adicionada.
- Todos os estilos estão em `globals.css`.
- Variáveis CSS mantêm paleta sóbria e profissional.
- Estados vazios e loading possuem ícones e alinhamento centralizado.
