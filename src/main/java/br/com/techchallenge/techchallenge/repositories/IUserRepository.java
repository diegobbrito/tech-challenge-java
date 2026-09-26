package br.com.techchallenge.techchallenge.repositories;

import br.com.techchallenge.techchallenge.entities.User;

import java.util.Optional;

public interface IUserRepository {
    Optional<User> findByUserLogin(String userLogin);
    Optional<User> findByEmail(String email);
}
