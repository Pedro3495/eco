CREATE TABLE accounts (
                          id UUID PRIMARY KEY,
                          name VARCHAR(80) NOT NULL,
                          type VARCHAR(50) NOT NULL,
                          initial_balance NUMERIC(15, 2) NOT NULL,
                          active BOOLEAN NOT NULL DEFAULT TRUE,
                          created_at TIMESTAMP NOT NULL,
                          updated_at TIMESTAMP NOT NULL
);

ALTER TABLE accounts
    ADD CONSTRAINT uk_accounts_name UNIQUE (name);
