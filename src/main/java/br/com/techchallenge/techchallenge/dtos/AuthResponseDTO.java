package br.com.techchallenge.techchallenge.dtos;

public record AuthResponseDTO(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn
) {
}