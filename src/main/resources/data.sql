CREATE TABLE usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    usuario_login VARCHAR(255) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    data_ultima_alteracao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    endereco VARCHAR(255) NOT NULL,
    tipo_usuario VARCHAR(7) NOT NULL
);

INSERT INTO usuario (nome, email, usuario_login, senha, endereco, tipo_usuario) VALUES
    ('João Silva', 'js@js.com', 'loginjs', '1234', 'Rua Dez, 500 - Centro', 'owner');

INSERT INTO usuario (nome, email, usuario_login, senha, endereco, tipo_usuario) VALUES
    ('Maria José', 'mariaj@mj.com', 'mariaj', '1234', 'Rua Trinta, 250 - Centro', 'user');
