package br.com.techchallenge.techchallenge.controllers;

import br.com.techchallenge.techchallenge.dtos.AuthResponseDTO;
import br.com.techchallenge.techchallenge.dtos.LoginRequestDTO;
import br.com.techchallenge.techchallenge.dtos.RefreshTokenRequestDTO;
import br.com.techchallenge.techchallenge.dtos.RotatedRefreshToken;
import br.com.techchallenge.techchallenge.entities.User;
import br.com.techchallenge.techchallenge.repositories.UserRepository;
import br.com.techchallenge.techchallenge.services.JwtService;
import br.com.techchallenge.techchallenge.services.RefreshTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    @Value("${security.jwt.access-token-expiration}")
    private long accessTokenExpiration;

    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository,
                          JwtService jwtService,
                          RefreshTokenService refreshTokenService) {

        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    public AuthResponseDTO login(@RequestBody LoginRequestDTO request) {

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        User user = userRepository.findByUserLogin(request.username())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        String accessToken = jwtService.generateToken(user);
        String refreshToken = refreshTokenService.create(user);
        return new AuthResponseDTO(accessToken, refreshToken, "Bearer", accessTokenExpiration / 1000);
    }

    @PostMapping("/refresh")
    public AuthResponseDTO refresh(@RequestBody RefreshTokenRequestDTO request) {
        RotatedRefreshToken rotated = refreshTokenService.rotate(request.refreshToken());
        String accessToken = jwtService.generateToken(rotated.user());
        return new AuthResponseDTO(accessToken, rotated.refreshToken(), "Bearer", accessTokenExpiration / 1000);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestBody RefreshTokenRequestDTO request) {
        refreshTokenService.revoke(request.refreshToken());
    }
}