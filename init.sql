CREATE TABLE usuario (
                         id BIGSERIAL PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         email VARCHAR(255) UNIQUE NOT NULL,
                         user_login VARCHAR(255) UNIQUE NOT NULL,
                         password VARCHAR(255) NOT NULL,
                         last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         address VARCHAR(255) NOT NULL,
                         user_type VARCHAR(7) NOT NULL
);