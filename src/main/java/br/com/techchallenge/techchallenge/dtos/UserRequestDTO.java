package br.com.techchallenge.techchallenge.dtos;

import jakarta.validation.constraints.NotNull;

public record UserRequestDTO(
        @NotNull(message = "Nome é obrigatório")
        String name,
        @NotNull(message = "Email é obrigatório")
        String email,
        @NotNull(message = "Login é obrigatório")
        String userLogin,
        @NotNull(message = "Senha é obrigatória")
        String password,
        @NotNull(message = "Endereço é obrigatório")
        String address,
        @NotNull(message = "Tipo de usuário é obrigatório")
        String userType
) {
}
