create table refresh_tokens (
                                id uuid primary key,
                                user_id uuid not null references users(id),
                                token_hash varchar(255) not null,
                                expires_at timestamp with time zone not null,
                                revoked_at timestamp with time zone,
                                created_at timestamp with time zone not null
);

create index idx_refresh_tokens_user_id on refresh_tokens(user_id);
create index idx_refresh_tokens_token_hash on refresh_tokens(token_hash);
