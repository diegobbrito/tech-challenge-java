CREATE TABLE usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    user_login VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    address VARCHAR(255) NOT NULL,
    user_type VARCHAR(7) NOT NULL
);

INSERT INTO usuario (name, email, user_login, password, address, user_type) VALUES
    ('João Silva', 'js@js.com', 'loginjs', '1234', 'Rua Dez, 500 - Centro', 'owner');

INSERT INTO usuario (name, email, user_login, password, address, user_type) VALUES
    ('Maria José', 'mariaj@mj.com', 'mariaj', '1234', 'Rua Trinta, 250 - Centro', 'user');
