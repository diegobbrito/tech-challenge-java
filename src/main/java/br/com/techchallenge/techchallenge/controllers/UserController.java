package br.com.techchallenge.techchallenge.controllers;

import br.com.techchallenge.techchallenge.dtos.UserRequestDTO;
import br.com.techchallenge.techchallenge.dtos.UserResponseDTO;
import br.com.techchallenge.techchallenge.dtos.UserTypeUpdateDTO;
import br.com.techchallenge.techchallenge.services.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<Page<UserResponseDTO>> findAllUser(@RequestParam("page") int page,
                                                            @RequestParam("size") int size) {
        return ResponseEntity.ok(userService.findAllUser(page, size));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponseDTO> findUserById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.findUserById(id));
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponseDTO> saveUser(@Valid @RequestBody UserRequestDTO userDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.saveUser(userDTO));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable("id") Long id, @RequestBody UserRequestDTO user) {
        return ResponseEntity.ok(userService.updateUser(user, id));
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/users/{id}/type")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> updateUserType(@PathVariable Long id, @RequestBody UserTypeUpdateDTO dto) {
        return ResponseEntity.ok(userService.updateUserType(id, dto.userType()));
    }
}
