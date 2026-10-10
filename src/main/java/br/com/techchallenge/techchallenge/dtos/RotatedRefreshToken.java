package br.com.techchallenge.techchallenge.dtos;

import br.com.techchallenge.techchallenge.entities.User;

public record RotatedRefreshToken(
        User user,
        String refreshToken
) {
}
