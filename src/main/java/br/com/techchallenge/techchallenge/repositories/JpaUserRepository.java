package br.com.techchallenge.techchallenge.repositories;

import br.com.techchallenge.techchallenge.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaUserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserLogin(String userLogin);
    Optional<User> findByEmail(String email);
}
