package br.com.techchallenge.techchallenge.services;

import br.com.techchallenge.techchallenge.enumerator.UserType;
import br.com.techchallenge.techchallenge.dtos.UserRequestDTO;
import br.com.techchallenge.techchallenge.entities.User;
import br.com.techchallenge.techchallenge.repositories.IUserRepository;
import br.com.techchallenge.techchallenge.repositories.UserRepository;
import br.com.techchallenge.techchallenge.services.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<User> findAllUser(int page, int size) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<User> users = this.userRepository.findAll(pageable);
        if (users.isEmpty()) {
            throw new ResourceNotFoundException("No users found");
        }
        return users;
    }

    public User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User by ID not found!"));
    }

    @Transactional
    public User saveUser(UserRequestDTO requestDTO) {
        User user = new User(requestDTO);
        user.setPassword(passwordEncoder.encode(requestDTO.password()));
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(UserRequestDTO requestDTO, Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setName(requestDTO.name());
        user.setEmail(requestDTO.email());
        user.setUserLogin(requestDTO.userLogin());
        user.setAddress(requestDTO.address());
        user.setUserType(UserType.valueOf(requestDTO.userType().toUpperCase()));
        if (requestDTO.password() != null && !requestDTO.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(requestDTO.password()));
        }
        return user;
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userRepository.delete(user);
    }
}