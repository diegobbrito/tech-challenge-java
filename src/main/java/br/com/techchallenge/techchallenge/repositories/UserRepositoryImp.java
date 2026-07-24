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
                        name AS name,
                        email AS email,
                        user_login AS userLogin,
                        password AS password,
                        last_modified_date AS lastModifiedDate,
                        address AS address,
                        user_type AS userType
                    FROM users WHERE id = :id
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
                        name,
                        email,
                        user_login AS userLogin,
                        password,
                        last_modified_date AS lastModifiedDate,
                        address,
                        user_type AS userType
                    FROM users LIMIT :size OFFSET :offset
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
                    INSERT INTO users (name, email, user_login, password, address, user_type)
                    VALUES (:name, :email, :user_login, :password, :address, :user_type)
                    """)
            .param("name", user.getName())
            .param("email", user.getEmail())
            .param("user_login", user.getUserLogin())
            .param("password", user.getPassword())
            .param("address", user.getAddress())
            .param("user_type", user.getUserType())
            .update();
    }

    @Override
    public Integer update(User user, Long id) {
        return this.jdbcClient
            .sql("""
                    UPDATE users SET
                    name = :name,
                    email = :email,
                    user_login = :user_login,
                    password = :password,
                    address = :address,
                    user_type = :user_type
                    WHERE id = :id
                    """
            )
            .param("id", id)
            .param("name", user.getName())
            .param("email", user.getEmail())
            .param("user_login", user.getUserLogin())
            .param("password", user.getPassword())
            .param("address", user.getAddress())
            .param("user_type", user.getUserType())
            .update();
    }

    @Override
    public Integer delete(Long id) {
        return this.jdbcClient
                .sql("""
                        DELETE FROM users WHERE id = :id
                        """)
                .param("id", id)
                .update();
    }
}
