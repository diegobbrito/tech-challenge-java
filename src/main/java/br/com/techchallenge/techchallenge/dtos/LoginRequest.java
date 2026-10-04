package br.com.techchallenge.techchallenge.dtos;

public record LoginRequest(
        String username,
        String password
) {
}