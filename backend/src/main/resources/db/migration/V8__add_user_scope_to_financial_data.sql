ALTER TABLE categories
    ADD COLUMN user_id UUID;

UPDATE categories
SET user_id = '11111111-1111-1111-1111-111111111111'
WHERE user_id IS NULL;

ALTER TABLE categories
    ALTER COLUMN user_id SET NOT NULL,
    ADD CONSTRAINT fk_categories_user FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE categories
    DROP CONSTRAINT uk_categories_name;

ALTER TABLE categories
    ADD CONSTRAINT uk_categories_user_name UNIQUE (user_id, name);

ALTER TABLE accounts
    ADD COLUMN user_id UUID;

UPDATE accounts
SET user_id = '11111111-1111-1111-1111-111111111111'
WHERE user_id IS NULL;

ALTER TABLE accounts
    ALTER COLUMN user_id SET NOT NULL,
    ADD CONSTRAINT fk_accounts_user FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE accounts
    DROP CONSTRAINT uk_accounts_name;

ALTER TABLE accounts
    ADD CONSTRAINT uk_accounts_user_name UNIQUE (user_id, name);

ALTER TABLE transactions
    ADD COLUMN user_id UUID;

UPDATE transactions
SET user_id = '11111111-1111-1111-1111-111111111111'
WHERE user_id IS NULL;

ALTER TABLE transactions
    ALTER COLUMN user_id SET NOT NULL,
    ADD CONSTRAINT fk_transactions_user FOREIGN KEY (user_id) REFERENCES users (id);
