package br.com.techchallenge.techchallenge.services;

import br.com.techchallenge.techchallenge.entities.User;
import br.com.techchallenge.techchallenge.enumerator.UserType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private JwtEncoder jwtEncoder;

    @InjectMocks
    private JwtService jwtService;

    @BeforeEach
    void setExpiration() {
        ReflectionTestUtils.setField(jwtService, "accessTokenExpiration", 300_000L);
    }

    @Test
    void generateTokenEncodesSubjectRoleAndExpiration() {
        User user = User.builder()
                .userLogin("login")
                .userType(UserType.ADMIN)
                .build();
        when(jwtEncoder.encode(any(JwtEncoderParameters.class)))
                .thenReturn(Jwt.withTokenValue("generated-token")
                        .header("alg", "HS256")
                        .claim("sub", "login")
                        .build());
        ArgumentCaptor<JwtEncoderParameters> parametersCaptor =
                ArgumentCaptor.forClass(JwtEncoderParameters.class);
        Instant before = Instant.now();

        String result = jwtService.generateToken(user);

        Instant after = Instant.now();
        verify(jwtEncoder).encode(parametersCaptor.capture());
        assertEquals("generated-token", result);
        assertEquals("login", parametersCaptor.getValue().getClaims().getSubject());
        assertEquals("ADMIN", parametersCaptor.getValue().getClaims().getClaim("role"));
        assertFalse(parametersCaptor.getValue().getClaims().getIssuedAt().isBefore(before));
        assertFalse(parametersCaptor.getValue().getClaims().getIssuedAt().isAfter(after));
        assertEquals(300, parametersCaptor.getValue().getClaims().getExpiresAt()
                .getEpochSecond() - parametersCaptor.getValue().getClaims().getIssuedAt().getEpochSecond());
    }
}
