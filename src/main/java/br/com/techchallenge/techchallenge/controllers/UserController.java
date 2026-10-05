package br.com.techchallenge.techchallenge.controllers;

import br.com.techchallenge.techchallenge.dtos.UserRequestDTO;
import br.com.techchallenge.techchallenge.entities.User;
import br.com.techchallenge.techchallenge.services.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<Page<User>> findAllUser(@RequestParam("page") int page,
                                                  @RequestParam("size") int size) {
        return ResponseEntity.ok(userService.findAllUser(page, size));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> findUserById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.findUserById(id));
    }

    @PostMapping("/users")
    public ResponseEntity<User> saveUser(@Valid @RequestBody UserRequestDTO userDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.saveUser(userDTO));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") Long id,
                                           @RequestBody UserRequestDTO user) {
        return ResponseEntity.ok(userService.updateUser(user, id));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}
