package br.com.techchallenge.techchallenge.services;

import br.com.techchallenge.techchallenge.dtos.UserRequestDTO;
import br.com.techchallenge.techchallenge.entities.User;
import br.com.techchallenge.techchallenge.repositories.UserRepository;
import br.com.techchallenge.techchallenge.services.exceptions.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAllUser(int page, int size) {
        int offset = (page - 1) * size;
        List<User> users = this.userRepository.findAll(size, offset);

        if (users.isEmpty()) {
            throw new ResourceNotFoundException("Nenhum usuário encontrado");
        }

        return users;
    }

    public Optional<User> findUserById(Long id){
        return Optional.of(this.userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário por ID não encontrado!")));
    }

    public void saveUser(UserRequestDTO requestDTO) {
        User userEntity = new User(requestDTO);
        userEntity.setPassword(passwordEncoder.encode(requestDTO.password()));
        var save = this.userRepository.save(userEntity);
        Assert.state(save == 1, "Erro ao salvar usuário: " + requestDTO.name());
    }

    public void updateUser(User user, Long id){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        var update = this.userRepository.update(user, id);
        if (update == 0) {
            throw new RuntimeException("Usuário não encontrado");
        }
    }

    public void deleteUser(Long id){
        var delete = this.userRepository.delete(id);
        if (delete == 0) {
            throw new RuntimeException("Usuário nao encontrado");
        }
    }



}
