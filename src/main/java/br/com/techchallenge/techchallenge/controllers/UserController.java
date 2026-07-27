package br.com.techchallenge.techchallenge.controllers;

import br.com.techchallenge.techchallenge.dtos.UserRequestDTO;
import br.com.techchallenge.techchallenge.entities.User;
import br.com.techchallenge.techchallenge.services.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequestMapping("/users")
@RestController
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> findAllUser(
            @RequestParam("page") int page,
            @RequestParam("size") int size
    ) {
        logger.info("GET -> /users");
        var listUsers = this.userService.findAllUser(page, size);
        return ResponseEntity.ok(listUsers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<User>> findUserById(
            @PathVariable("id") Long id
    ) {
        logger.info("GET -> /users/{id}", id);
        var listUser = this.userService.findUserById(id);
        return ResponseEntity.ok(listUser);
    }

    @PostMapping
    public ResponseEntity<Void> saveUser(
            @Valid @RequestBody UserRequestDTO userDTO
    ) {
        logger.info("POST -> /users");
        this.userService.saveUser(userDTO);
        return ResponseEntity.status(201).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(
            @PathVariable("id") Long id,
            @RequestBody User user
    ) {
        logger.info("PUT -> /users");
        this.userService.updateUser(user, id);
        var status = HttpStatus.NO_CONTENT;
        return ResponseEntity.status(status.value()).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable("id") Long id
    ) {
        logger.info("DELETE -> /users");
        this.userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}
