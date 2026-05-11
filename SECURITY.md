# Security

Eco tem autenticacao JWT e dados financeiros por usuario. Ainda assim, este repositorio e um projeto de portfolio e estudo, nao um produto financeiro pronto para producao.

## Reportar Problemas

Se encontrar uma vulnerabilidade, abra uma issue descrevendo:

- impacto;
- passos para reproduzir;
- endpoint ou arquivo afetado;
- sugestao de correcao, se houver.

Nao publique tokens, senhas reais ou dados financeiros reais em issues.

## Cuidados Locais

- Nao commitar `.env.local`.
- Trocar `JWT_SECRET` fora do ambiente de desenvolvimento.
- Usar HTTPS em deploy real.
- Revisar CORS antes de producao.
- Usar senhas fortes para usuarios reais.

