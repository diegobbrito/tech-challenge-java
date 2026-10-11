package br.com.techchallenge.techchallenge.dtos;

import jakarta.validation.constraints.NotNull;

public record UserRequestDTO(
        @NotNull(message = "Name is required")
        String name,
        @NotNull(message = "Email is required")
        String email,
        @NotNull(message = "Login is required")
        String userLogin,
        @NotNull(message = "Password is required")
        String password,
        @NotNull(message = "Address is required")
        String address,
        @NotNull(message = "User type is required")
        String userType
) {
}
