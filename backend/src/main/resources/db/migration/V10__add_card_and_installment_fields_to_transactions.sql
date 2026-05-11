ALTER TABLE transactions
    ADD COLUMN billing_month VARCHAR(7),
    ADD COLUMN installment_group_id UUID,
    ADD COLUMN installment_number INTEGER,
    ADD COLUMN installment_total INTEGER;
