CREATE TABLE goals (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    name VARCHAR(120) NOT NULL,
    target_amount NUMERIC(15, 2) NOT NULL,
    current_amount NUMERIC(15, 2) NOT NULL,
    target_date DATE,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_goals_user FOREIGN KEY (user_id) REFERENCES users (id)
);

