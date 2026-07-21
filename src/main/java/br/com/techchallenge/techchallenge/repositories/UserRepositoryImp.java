package br.com.techchallenge.techchallenge.repositories;

import br.com.techchallenge.techchallenge.entities.User;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImp implements UserRepository {

    private final JdbcClient jdbcClient;
    public UserRepositoryImp(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Optional<User> findById(Long id) {
        return this.jdbcClient
            .sql("""
                    SELECT
                        id,
                        nome AS name,
                        email AS email,
                        usuario_login AS userLogin,
                        senha AS password,
                        data_ultima_alteracao AS lastModifiedDate,
                        endereco AS address,
                        tipo_usuario AS userType
                    FROM usuario WHERE id = :id
                    """
            )
            .param("id", id)
            .query(User.class)
            .optional();
    }

    @Override
    public List<User> findAll(int size, int offset) {
        return this.jdbcClient
            .sql("""
                    SELECT
                        id,
                        nome AS name,
                        email AS email,
                        usuario_login AS userLogin,
                        senha AS password,
                        data_ultima_alteracao AS lastModifiedDate,
                        endereco AS address,
                        tipo_usuario AS userType
                    FROM usuario LIMIT :size OFFSET :offset
                    """
            )
            .param("size", size)
            .param("offset", offset)
            .query(User.class)
            .list();
    }

    @Override
    public Integer save(User user) {
        return this.jdbcClient
            .sql("""
                    INSERT INTO usuario (nome, email, usuario_login, senha, endereco, tipo_usuario)
                    VALUES (:nome, :email, :usuario_login, :senha, :endereco, :tipo_usuario)
                    """)
            .param("nome", user.getName())
            .param("email", user.getEmail())
            .param("usuario_login", user.getUserLogin())
            .param("senha", user.getPassword())
            .param("endereco", user.getAddress())
            .param("tipo_usuario", user.getUserType())
            .update();
    }

    @Override
    public Integer update(User user, Long id) {
        return this.jdbcClient
            .sql("""
                    UPDATE usuario SET
                    nome = :nome,
                    email = :email,
                    usuario_login = :usuario_login,
                    senha = :senha,
                    endereco = :endereco,
                    tipo_usuario = :tipo_usuario
                    WHERE id = :id
                    """
            )
            .param("id", id)
            .param("nome", user.getName())
            .param("email", user.getEmail())
            .param("usuario_login", user.getUserLogin())
            .param("senha", user.getPassword())
            .param("endereco", user.getAddress())
            .param("tipo_usuario", user.getUserType())
            .update();
    }

    @Override
    public Integer delete(Long id) {
        return this.jdbcClient
                .sql("""
                        DELETE FROM usuario WHERE id = :id
                        """)
                .param("id", id)
                .update();
    }
}
