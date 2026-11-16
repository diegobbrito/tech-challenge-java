package br.com.techchallenge.techchallenge.services;

import br.com.techchallenge.techchallenge.dtos.RotatedRefreshToken;
import br.com.techchallenge.techchallenge.entities.RefreshToken;
import br.com.techchallenge.techchallenge.entities.User;
import br.com.techchallenge.techchallenge.enumerator.UserType;
import br.com.techchallenge.techchallenge.repositories.IRefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private IRefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @BeforeEach
    void setExpiration() {
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenExpiration", 60_000L);
        lenient().when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void createStoresOnlyHashAndReturnsRawToken() {
        User user = user();
        ArgumentCaptor<RefreshToken> tokenCaptor = ArgumentCaptor.forClass(RefreshToken.class);

        String rawToken = refreshTokenService.create(user);

        assertNotNull(rawToken);
        assertFalse(rawToken.isBlank());
        verify(refreshTokenRepository).save(tokenCaptor.capture());
        RefreshToken storedToken = tokenCaptor.getValue();
        assertNotEquals(rawToken, storedToken.getTokenHash());
        assertEquals(user, storedToken.getUser());
        assertFalse(storedToken.isRevoked());
        assertTrue(storedToken.getExpiresAt().isAfter(Instant.now()));
    }

    @Test
    void rotateRevokesOldTokenAndCreatesANewTokenForSameUser() {
        User user = user();
        RefreshToken current = validToken(user);
        when(refreshTokenRepository.findByTokenHash(anyString())).thenReturn(Optional.of(current));

        RotatedRefreshToken rotated = refreshTokenService.rotate("old-token");

        assertSame(user, rotated.user());
        assertNotEquals("old-token", rotated.refreshToken());
        assertTrue(current.isRevoked());
        verify(refreshTokenRepository, times(2)).save(any(RefreshToken.class));
    }

    @Test
    void rotateRejectsUnknownToken() {
        when(refreshTokenRepository.findByTokenHash(anyString())).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> refreshTokenService.rotate("unknown"));
        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
    }

    @Test
    void rotateRejectsExpiredToken() {
        RefreshToken expired = validToken(user());
        expired.setExpiresAt(Instant.now().minusSeconds(1));
        when(refreshTokenRepository.findByTokenHash(anyString())).thenReturn(Optional.of(expired));

        assertThrows(BadCredentialsException.class, () -> refreshTokenService.rotate("expired"));
        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
    }

    @Test
    void rotateRejectsRevokedToken() {
        RefreshToken revoked = validToken(user());
        revoked.setRevoked(true);
        when(refreshTokenRepository.findByTokenHash(anyString())).thenReturn(Optional.of(revoked));

        assertThrows(BadCredentialsException.class, () -> refreshTokenService.rotate("revoked"));
        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
    }

    @Test
    void revokeMarksValidTokenAsRevoked() {
        RefreshToken token = validToken(user());
        when(refreshTokenRepository.findByTokenHash(anyString())).thenReturn(Optional.of(token));

        refreshTokenService.revoke("valid-token");

        assertTrue(token.isRevoked());
        verify(refreshTokenRepository).save(token);
    }

    @Test
    void revokeRejectsUnknownExpiredAndRevokedTokens() {
        RefreshToken expired = validToken(user());
        expired.setExpiresAt(Instant.now().minusSeconds(1));
        RefreshToken revoked = validToken(user());
        revoked.setRevoked(true);
        when(refreshTokenRepository.findByTokenHash(anyString()))
                .thenReturn(Optional.empty(), Optional.of(expired), Optional.of(revoked));

        assertThrows(BadCredentialsException.class, () -> refreshTokenService.revoke("unknown"));
        assertThrows(BadCredentialsException.class, () -> refreshTokenService.revoke("expired"));
        assertThrows(BadCredentialsException.class, () -> refreshTokenService.revoke("revoked"));

        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
    }

    private static RefreshToken validToken(User user) {
        RefreshToken token = new RefreshToken();
        token.setTokenHash("stored-hash");
        token.setUser(user);
        token.setExpiresAt(Instant.now().plusSeconds(60));
        return token;
    }

    private static User user() {
        return User.builder()
                .id(1L)
                .name("Test User")
                .email("user@example.com")
                .userLogin("login")
                .password("encoded")
                .userType(UserType.USER)
                .build();
    }
}
