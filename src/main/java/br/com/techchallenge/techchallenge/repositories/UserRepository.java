package br.com.techchallenge.techchallenge.repositories;

import br.com.techchallenge.techchallenge.entities.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepository implements IUserRepository {

    private final JpaUserRepository repository;

    public UserRepository(JpaUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<User> findById(Long id) {
        return this.repository.findById(id);
    }

    @Override
    public Optional<User> findByUserLogin(String userLogin) {
        return this.repository.findByUserLogin(userLogin);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return this.repository.findByEmail(email);
    }

    @Override
    public User save(User user) {
        return this.repository.save(user);
    }

    @Override
    public Void delete(User user) {
        this.repository.delete(user);
        return null;
    }

}