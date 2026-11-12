package br.com.techchallenge.techchallenge.services;

import br.com.techchallenge.techchallenge.dtos.UserRequestDTO;
import br.com.techchallenge.techchallenge.dtos.UserResponseDTO;
import br.com.techchallenge.techchallenge.entities.User;
import br.com.techchallenge.techchallenge.enumerator.UserType;
import br.com.techchallenge.techchallenge.repositories.UserRepository;
import br.com.techchallenge.techchallenge.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void returnSavedUser() {
        lenient().when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void findAllUserReturnsMappedPage() {
        User user = user(1L);
        when(userRepository.findAll(PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(List.of(user)));

        Page<UserResponseDTO> result = userService.findAllUser(1, 10);

        assertEquals(1, result.getTotalElements());
        assertEquals("login1", result.getContent().getFirst().userLogin());
    }

    @Test
    void findAllUserThrowsWhenPageIsEmpty() {
        when(userRepository.findAll(PageRequest.of(0, 10)))
                .thenReturn(Page.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.findAllUser(1, 10));
    }

    @Test
    void findUserByIdReturnsMappedUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1L)));

        UserResponseDTO result = userService.findUserById(1L);

        assertEquals(1L, result.id());
        assertEquals("USER", result.userType());
    }

    @Test
    void findUserByIdThrowsWhenUserDoesNotExist() {
        when(userRepository.findById(8L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.findUserById(8L));
    }

    @Test
    void saveUserEncodesPasswordAndDefaultsToUserRole() {
        UserRequestDTO request = request("clear-password");
        when(passwordEncoder.encode("clear-password")).thenReturn("encoded-password");

        UserResponseDTO result = userService.saveUser(request);

        assertEquals("login", result.userLogin());
        assertEquals("USER", result.userType());
        verify(passwordEncoder).encode("clear-password");
        verify(userRepository).save(argThat(user ->
                "encoded-password".equals(user.getPassword()) && user.getUserType() == UserType.USER));
    }

    @Test
    void updateUserKeepsExistingPasswordWhenRequestPasswordIsBlank() {
        User existing = user(1L);
        existing.setPassword("existing-encoded-password");
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));

        userService.updateUser(request("  "), 1L);

        assertEquals("existing-encoded-password", existing.getPassword());
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository).save(existing);
    }

    @Test
    void updateUserEncodesANewPassword() {
        User existing = user(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(passwordEncoder.encode("new-password")).thenReturn("new-encoded-password");

        userService.updateUser(request("new-password"), 1L);

        assertEquals("new-encoded-password", existing.getPassword());
        verify(passwordEncoder).encode("new-password");
    }

    @Test
    void updateUserThrowsWhenUserDoesNotExist() {
        when(userRepository.findById(8L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(request("password"), 8L));
    }

    @Test
    void deleteUserRemovesExistingUser() {
        User existing = user(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));

        userService.deleteUser(1L);

        verify(userRepository).delete(existing);
    }

    @Test
    void deleteUserThrowsWhenUserDoesNotExist() {
        when(userRepository.findById(8L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(8L));
        verify(userRepository, never()).delete(any());
    }

    @Test
    void updateUserTypePersistsNewRole() {
        User existing = user(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));

        UserResponseDTO result = userService.updateUserType(1L, UserType.ADMIN);

        assertEquals("ADMIN", result.userType());
        verify(userRepository).save(existing);
    }

    private static User user(Long id) {
        return User.builder()
                .id(id)
                .name("Test User")
                .email("user" + id + "@example.com")
                .userLogin("login" + id)
                .password("encoded")
                .address("Test address")
                .userType(UserType.USER)
                .build();
    }

    private static UserRequestDTO request(String password) {
        return new UserRequestDTO("Test User", "user@example.com", "login", password, "Test address");
    }
}
