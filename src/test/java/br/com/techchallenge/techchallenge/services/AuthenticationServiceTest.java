package br.com.techchallenge.techchallenge.services;

import br.com.techchallenge.techchallenge.dtos.LoginRequestDTO;
import br.com.techchallenge.techchallenge.dtos.UserRequestDTO;
import br.com.techchallenge.techchallenge.entities.User;
import br.com.techchallenge.techchallenge.enumerator.UserType;
import br.com.techchallenge.techchallenge.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void signupEncodesPasswordBeforeSavingUser() {
        UserRequestDTO request = new UserRequestDTO(
                "Test User", "user@example.com", "login", "plain-password", "Test address");
        when(passwordEncoder.encode("plain-password")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        User result = authenticationService.signup(request);

        verify(userRepository).save(userCaptor.capture());
        assertSame(userCaptor.getValue(), result);
        assertEquals("encoded-password", result.getPassword());
        assertEquals("login", result.getUsername());
        assertEquals("Test address", result.getAddress());
        assertEquals(UserType.USER, result.getUserType());
    }

    @Test
    void authenticateChecksCredentialsAndReturnsUser() {
        User user = User.builder().userLogin("login").userType(UserType.USER).build();
        when(userRepository.findByUserLogin("login")).thenReturn(Optional.of(user));
        LoginRequestDTO request = new LoginRequestDTO("login", "password");

        User result = authenticationService.authenticate(request);

        assertSame(user, result);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByUserLogin("login");
    }

    @Test
    void authenticatePropagatesFailureWhenUserIsMissing() {
        when(userRepository.findByUserLogin("missing")).thenReturn(Optional.empty());

        assertThrows(java.util.NoSuchElementException.class,
                () -> authenticationService.authenticate(new LoginRequestDTO("missing", "password")));

        verify(authenticationManager).authenticate(any(Authentication.class));
    }
}
