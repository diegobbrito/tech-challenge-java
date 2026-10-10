package br.com.techchallenge.techchallenge.dtos;

public record UserResponseDTO(
        Long id,
        String name,
        String email,
        String userLogin,
        String address,
        String userType
) {
}
