CREATE TABLE categories (
    id UUID PRIMARY KEY,
    name VARCHAR(80) NOT NULL,
    kind VARCHAR(20) NOT NULL,
    color VARCHAR(20),
    icon VARCHAR(50),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

ALTER TABLE categories
    ADD CONSTRAINT uk_categories_name UNIQUE (name);
