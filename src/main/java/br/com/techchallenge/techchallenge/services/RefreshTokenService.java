package br.com.techchallenge.techchallenge.services;

import br.com.techchallenge.techchallenge.dtos.RotatedRefreshToken;
import br.com.techchallenge.techchallenge.entities.RefreshToken;
import br.com.techchallenge.techchallenge.entities.User;
import br.com.techchallenge.techchallenge.repositories.IRefreshTokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service
public class RefreshTokenService {

    private final IRefreshTokenRepository refreshTokenRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${security.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    public RefreshTokenService(IRefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public String create(User user) {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);

        String rawToken = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setTokenHash(hashToken(rawToken));
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(Instant.now().plusMillis(refreshTokenExpiration));
        refreshToken.setRevoked(false);

        refreshTokenRepository.save(refreshToken);

        return rawToken;
    }

    @Transactional
    public RotatedRefreshToken rotate(String rawToken) {
        RefreshToken current = findValidToken(rawToken);

        current.setRevoked(true);
        refreshTokenRepository.save(current);

        User user = current.getUser();
        String newRefreshToken = create(user);

        return new RotatedRefreshToken(user, newRefreshToken);
    }

    @Transactional
    public void revoke(String rawToken) {
        RefreshToken token = refreshTokenRepository
                .findByTokenHash(hashToken(rawToken))
                .orElseThrow(() ->
                        new BadCredentialsException("Invalid refresh token"));

        if (token.isRevoked() || isExpired(token)) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        token.setRevoked(true);
        refreshTokenRepository.save(token);
    }

    private RefreshToken findValidToken(String rawToken) {
        RefreshToken token = refreshTokenRepository
                .findByTokenHash(hashToken(rawToken))
                .orElseThrow(() ->
                        new BadCredentialsException("Invalid refresh token"));

        if (token.isRevoked() || isExpired(token)) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        return token;
    }

    private boolean isExpired(RefreshToken token) {
        return !token.getExpiresAt().isAfter(Instant.now());
    }

    private String hashToken(String rawToken) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(rawToken.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }
}
