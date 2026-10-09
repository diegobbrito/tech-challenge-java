CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    user_login VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    user_type VARCHAR(7) NOT NULL
);

INSERT INTO users (name, email, user_login, password, address, user_type) VALUES
    ('Admin', 'admin@admin.com', 'admin', '$2a$10$pXAYJvNUBq/00VbTwKDoYecy5BJIgVTKUimFCEQzWwYB.UELYggBq', 'Rua Dez, 500 - Centro', 'ADMIN');