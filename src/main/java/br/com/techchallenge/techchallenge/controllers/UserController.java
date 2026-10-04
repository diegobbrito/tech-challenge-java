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
        var listUsers = this.userService.findAllUser(page, size);
        return ResponseEntity.ok(listUsers);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> findUserById(@PathVariable("id") Long id) {
        var listUser = this.userService.findUserById(id);
        return ResponseEntity.ok(listUser);
    }

    @PostMapping("/users")
    public ResponseEntity<Void> saveUser(@Valid @RequestBody UserRequestDTO userDTO) {
        this.userService.saveUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable("id") Long id,
                                           @RequestBody UserRequestDTO user) {
        this.userService.updateUser(user, id);
        var status = HttpStatus.NO_CONTENT;
        return ResponseEntity.status(status.value()).build();
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        this.userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}
