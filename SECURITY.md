# Security

Eco tem autenticacao JWT e dados financeiros por usuario. Ainda assim, este repositorio e um projeto de portfolio e estudo, nao um produto financeiro pronto para producao.

## Estado Atual Para Open Source

O codigo pode ficar publico como projeto de portfolio. Os principais cuidados de
seguranca para repositorio aberto ja estao aplicados:

- `ECO_JWT_SECRET` nao tem fallback no profile padrao.
- Credenciais do banco nao tem fallback no profile padrao.
- Seeds de desenvolvimento ficam no profile `dev`.
- Postgres local fica bindado em `127.0.0.1`.
- Tokens de auth ficam em cookies HttpOnly, nao em `localStorage`.
- Frontend foi auditado com `npm audit`.

Isso nao significa que uma instancia publica de producao esteja pronta sem
configuracao propria.

## Reportar Problemas

Se encontrar uma vulnerabilidade, abra uma issue descrevendo:

- impacto;
- passos para reproduzir;
- endpoint ou arquivo afetado;
- sugestao de correcao, se houver.

Nao publique tokens, senhas reais ou dados financeiros reais em issues.

## Cuidados Locais

- Nao commitar `.env`, `.env.local` ou dados financeiros reais.
- Trocar `ECO_JWT_SECRET` fora do ambiente de desenvolvimento.
- Rodar sem `SPRING_PROFILES_ACTIVE=dev` em producao.
- Manter `ECO_COOKIE_SECURE=true` quando usar HTTPS.
- Usar HTTPS em deploy real.
- Revisar CORS antes de producao.
- Usar senhas fortes para usuarios reais.
