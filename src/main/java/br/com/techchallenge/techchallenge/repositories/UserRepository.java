package br.com.techchallenge.techchallenge.repositories;

import br.com.techchallenge.techchallenge.entities.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    Optional<User> findByUserLogin(String userLogin);
    Optional<User> findById(Long id);
    List<User> findAll(int size, int offset);
    Integer save(User user);
    Integer update(User user, Long id);
    Integer delete(Long id);

}
