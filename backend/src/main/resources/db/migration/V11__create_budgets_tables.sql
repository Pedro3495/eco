CREATE TABLE monthly_budgets (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    month VARCHAR(7) NOT NULL,
    general_limit NUMERIC(15, 2),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_monthly_budgets_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT uk_monthly_budgets_user_month UNIQUE (user_id, month)
);

CREATE TABLE category_budgets (
    id UUID PRIMARY KEY,
    monthly_budget_id UUID NOT NULL,
    category_id UUID NOT NULL,
    limit_amount NUMERIC(15, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_category_budgets_monthly_budget FOREIGN KEY (monthly_budget_id) REFERENCES monthly_budgets (id),
    CONSTRAINT fk_category_budgets_category FOREIGN KEY (category_id) REFERENCES categories (id),
    CONSTRAINT uk_category_budgets_budget_category UNIQUE (monthly_budget_id, category_id)
);
