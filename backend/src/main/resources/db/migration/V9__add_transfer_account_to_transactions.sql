ALTER TABLE transactions
    ADD COLUMN transfer_account_id UUID;

ALTER TABLE transactions
    ALTER COLUMN category_id DROP NOT NULL;

ALTER TABLE transactions
    ADD CONSTRAINT fk_transactions_transfer_account FOREIGN KEY (transfer_account_id) REFERENCES accounts (id);
