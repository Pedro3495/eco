# Eco Frontend

Frontend em Next.js do Eco, um PWA pessoal de controle financeiro.

## Stack

- Next.js 16
- React 19
- TypeScript
- CSS puro (design tokens + dark mode)
- Recharts (gráficos)
- Framer Motion (animações)
- Lucide React (ícones)

## Rodar Localmente

Instalar dependências:

```powershell
cd frontend
npm install
```

Rodar em desenvolvimento:

```powershell
npm run dev
```

Abrir:

```text
http://localhost:3000/login
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

- Tela `/login` integrada ao `POST /auth/login`.
- Tokens salvos em `localStorage`.
- Client HTTP autenticado com `Authorization: Bearer <accessToken>`.
- Redirect para `/login` quando nao ha token ou quando a API retorna `401`.
- Logout basico com `POST /auth/logout`.
- Dashboard integrado ao backend com gráficos (fluxo de caixa, gastos por categoria).
- Resumo mensal via `GET /reports/monthly-summary`.
- Últimas transações via `GET /transactions`.
- Criação de transação via `POST /transactions`.
- Tela `/transactions` com filtros por período, tipo, conta e categoria.
- Edição via `PUT /transactions/{id}`.
- Exclusão via `DELETE /transactions/{id}`.
- Fallback para mocks quando a API está indisponível no dashboard.
- PWA base com manifest, tema mobile e ícone.
- Dark mode com toggle persistente.
- Bottom navigation no mobile.
- FAB (Floating Action Button) para nova transação no mobile.
- Telas adicionais: `/accounts`, `/categories`, `/budgets`, `/goals` (com mocks).

## Estrutura Relevante

```text
src/app/page.tsx                  <- Dashboard
src/app/login/page.tsx            <- Login
src/app/transactions/page.tsx     <- Transações
src/app/accounts/page.tsx         <- Contas (mock)
src/app/categories/page.tsx       <- Categorias (mock)
src/app/budgets/page.tsx          <- Orçamentos (mock)
src/app/goals/page.tsx            <- Metas (mock)
src/components/TransactionModal.tsx
src/components/Card.tsx
src/components/MetricCard.tsx
src/components/ProgressBar.tsx
src/components/BottomNav.tsx
src/components/FAB.tsx
src/components/AuthGuard.tsx
src/components/LogoutButton.tsx
src/components/ThemeToggle.tsx
src/components/PageShell.tsx
src/lib/api.ts
src/lib/format.ts
src/mocks/finance-data.ts
src/app/globals.css               <- Design system + dark mode
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

- Refresh token automatico quando o access token expirar.
- Redirecionar `/login` para `/` quando ja existir token valido.
- Usar `/auth/me` para mostrar nome/email do usuario logado.
- Service worker/offline cache mais completo.

## Decisões de Design e UX

### Mobile-first

O layout foi pensado para funcionar bem em telas pequenas antes de desktop:

- Shell reduz padding lateral em mobile (`16px` vs `24px`).
- Topbar mantém a marca e ações em uma única linha quando possível; em telas muito pequenas (`<380px`) os botões ocupam a largura total.
- Grid de métricas no dashboard passa de 3 colunas para 1 coluna em mobile.
- Lista de transações em mobile usa cards individuais em vez de linhas, facilitando o toque.
- Filtros em `/transactions` usam drawer slide-up no mobile e painel expandido no desktop.
- Paginação empilha botões em mobile para não haver overflow horizontal.
- Bottom navigation fixa no mobile para navegação rápida.
- FAB fixo no canto inferior direito para ação principal (nova transação).

### Dark Mode

- Variáveis CSS duplas (`:root` e `[data-theme="dark"]`).
- Toggle persiste em `localStorage` e respeita `prefers-color-scheme`.
- Todos os componentes usam tokens CSS, garantindo consistência.

### Animações

- Framer Motion para entrada de cards, modais e listas.
- CSS transitions para hover e estados interativos.
- `prefers-reduced-motion` respeitado globalmente.

### Modal Acessível

O modal de transação implementa padrões de acessibilidade:

- Fecha ao pressionar `ESC` (se não estiver salvando).
- Fecha ao clicar no backdrop (se não estiver salvando).
- Foco é movido para o primeiro campo ao abrir.
- Foco é retornado ao elemento que abriu o modal ao fechar.
- Trap de foco: `Tab` e `Shift+Tab` circulam apenas entre elementos do modal.
- Scroll do body é bloqueado enquanto o modal está aberto.
- `aria-labelledby`, `aria-describedby` e `role="dialog"` são usados.
- Slide-up no mobile (estilo app nativo), fade+scale no desktop.

### Acessibilidade Geral

- Skip link permite pular para o conteúdo principal.
- Todos os botões `icon-only` possuem `aria-label` descritivo.
- Ícones decorativos usam `aria-hidden="true"`.
- Estados de loading usam `aria-busy="true"` e `aria-live="polite"`.
- Mensagens de erro usam `role="alert"`.
- Inputs de formulário possuem `htmlFor`/`id` associados.
- Foco visível usa `box-shadow` semitransparente e `outline: none` apenas quando o foco é visível via `:focus-visible`.
- Animações respeitam `prefers-reduced-motion`.

### PWA

- Manifesto inclui `categories`, `lang` e ícones com purpose `any` e `maskable`.
- Viewport permite zoom (`maximum-scale: 5`, `user-scalable: true`) para acessibilidade.
- Metadata inclui `formatDetection: { telephone: false }` para evitar links automáticos de telefone.

### CSS Puro

- Nenhuma biblioteca de UI foi adicionada.
- Todos os estilos estão em `globals.css`.
- Variáveis CSS mantêm paleta premium com suporte a dark mode.
- Estados vazios e loading possuem ícones e alinhamento centralizado.
