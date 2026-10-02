package br.com.techchallenge.techchallenge.repositories;

import br.com.techchallenge.techchallenge.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface IUserRepository {
    Optional<User> findByUserLogin(String userLogin);
    Optional<User> findByEmail(String email);
    User save(User user);
    Optional<User> findById(Long id);
    void delete(User user);
    Page<User> findAll(Pageable pageable);
}
